#!/bin/bash

# ============================================================
# 强制全量部署入口 (单一 git 仓库版)
# ------------------------------------------------------------
# 与 maozi-cloud-build-all.sh 的区别:
#   - 不读取 / 写入 maozi-cloud-parent-env 状态 (分支 / SHA)
#   - 不做 git diff 增量比对
#   - 不做 jar mtime 前后快照对比
#   - 无条件: 全量 mvn 构建 + 所有有镜像 Dockerfile 的服务全部重建并重启
# ------------------------------------------------------------
# 工作流程:
#   1. 切换到仓库根目录
#   2. 构建基础镜像 maozi-cloud-base-jdk:1.0.0 (服务镜像 FROM 它, 必须先就绪)
#   3. mvn clean install -T 16C 全量构建整个 reactor
#   4. find 扫描 maozi-cloud-services 下所有 jar
#   5. 对每个 jar:
#        - 按服务名前缀路由镜像 / docker-compose 目录
#        - 校验镜像 Dockerfile ($image_dir/${service_name}-image) 存在才部署
#        - 生成临时 build-docker.sh: cp jar / buildx / compose up -d / 自清理
#        - 后台并行执行
#   6. wait 等所有后台部署完成
# ------------------------------------------------------------
# 适用场景:
#   - 首次部署
#   - 切换分支 / 合并代码后想强制重新部署一切
#   - base-jdk 镜像或 OTel agent 变更后需要全部重建
#   - 怀疑增量部署有遗漏, 想做一次彻底的回归
# ============================================================

# 切换到本脚本所在目录, 使后续相对路径可靠
cd "$(dirname "$0")"
current_directory="$(pwd)"

# 源码仓库根目录 (与 maozi-cloud-build-all.sh 保持一致)
repo_directory="/Users/maozi/maozi-cloud/maozi-cloud-parent"
cd "$repo_directory"

# 可部署服务源码根目录 (仅此目录下生成的 jar 才会触发 Docker 部署)
services_subdir="maozi-cloud-service/maozi-cloud-services"

# ============================================================
# 工具函数 (与 maozi-cloud-build-jar-utils.sh 保持一致)
# ============================================================

# 根据服务名前缀路由镜像目录
#   maozi-cloud-basics-* -> maozi-cloud-basics-image
#   其他                -> maozi-cloud-services-image
route_image_dir() {
    local service_name="$1"
    local base="$current_directory/../../maozi-cloud-image"
    case "$service_name" in
        maozi-cloud-basics-*) echo "$base/maozi-cloud-basics-image" ;;
        *)                    echo "$base/maozi-cloud-services-image" ;;
    esac
}

# 根据服务名前缀路由 docker-compose 目录
route_docker_dir() {
    local service_name="$1"
    local base="$current_directory/../../maozi-cloud-docker"
    case "$service_name" in
        maozi-cloud-basics-*) echo "$base/maozi-cloud-basics-docker" ;;
        *)                    echo "$base/maozi-cloud-services-docker" ;;
    esac
}

# ============================================================
# 1. 构建基础 JDK 镜像 (所有服务镜像 FROM 它)
# ============================================================
base_image_directory="$current_directory/../../maozi-cloud-image/maozi-cloud-base-jdk-image"

if [ -f "$base_image_directory/Dockerfile" ]; then
    echo "[base] building maozi-cloud-base-jdk:1.0.0"
    # base 镜像不依赖 jar, 先于 Maven 构建之前就绪, 服务镜像才能 FROM 到
    # 用 buildx 构建, 上下文为 Dockerfile 所在目录 (含 OTel agent jar)
    (cd "$base_image_directory" && docker buildx build -f Dockerfile -t maozi-cloud-base-jdk:1.0.0 .) || {
        echo "[base] FAILED: maozi-cloud-base-jdk:1.0.0 build error, abort"
        exit 1
    }
    echo "[base] done"
else
    echo "[base] skip: Dockerfile not found at $base_image_directory/Dockerfile"
fi

# ============================================================
# 2. 全量 Maven 构建 (不做 -pl / -amd 增量, 直接整个 reactor)
# ============================================================
echo "[build] mvn clean install (full reactor)"

# 复用 jar-utils 的参数:
#   -T 16C                    并行线程数 = 16 * CPU 核
#   -Dmaven.compile.fork=true 编译过程 fork 专属 JVM, 加快速度
#   -Dmaven.test.skip=true    跳过测试编译与执行, 加快部署
mvn clean install -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true

# Maven 失败立即中止, 不进入 Docker 部署阶段 (避免用旧 jar 重建镜像)
if [ $? -ne 0 ]; then
    echo "[build] FAILED: mvn clean install error, abort"
    exit 1
fi

# ============================================================
# 3. 扫描所有 jar, 过滤出可部署的服务
# ============================================================
# 锁定仓库根的绝对路径, 让生成的临时脚本里的 cp 源路径不依赖 cwd
repo_root="$(pwd)"

# find 所有 jar (包括首次部署时还没构建的情况也能正确跳过)
deploy_count=0
deploy_pids=()

# process substitution 避免 while 进入 subshell 丢变量
while IFS= read -r -d '' jarfile; do

    # dirname 两次: jar -> target -> 模块目录
    # business: maozi-cloud-business-xxx/maozi-cloud-xxx-run (启动模块)
    # basics:   maozi-cloud-basics-xxx (扁平单模块)
    module_dir="$(dirname "$(dirname "$jarfile")")"
    service_name="$(basename "$module_dir")"

    image_directory="$(route_image_dir "$service_name")"
    docker_directory="$(route_docker_dir "$service_name")"

    # 校验镜像目录下存在 {service_name}-image Dockerfile, 不存在则跳过
    # (api / service / all-service 等非启动模块没有 -image Dockerfile, 自动被过滤)
    if [ ! -e "$image_directory/${service_name}-image" ]; then
        echo "[deploy] skip $service_name: image file not found at $image_directory/${service_name}-image"
        continue
    fi

    # 动态生成 service_name-build-docker.sh: 拷贝 jar / 构建镜像 / compose 启动 / 清理自身
    build_script="$image_directory/${service_name}-build-docker.sh"
    {
        echo "#!/bin/bash"
        echo "cp \"$repo_root/$module_dir/target/${service_name}.jar\" \"$image_directory/\""
        echo "cd \"$image_directory\""
        echo "docker buildx build -f \"$image_directory/${service_name}-image\" -t \"${service_name}:laster\" ."
        echo "docker-compose -f \"$docker_directory/docker-compose.yml\" up -d ${service_name}"
        echo "rm -f \"$image_directory/${service_name}.jar\""
        echo "rm -f \"\$0\""
    } > "$build_script"
    chmod +x "$build_script"

    echo "[deploy] $service_name: building image and starting container"
    # 后台执行该 Docker 构建脚本, 多个服务可并行部署
    bash "$build_script" &
    deploy_pids+=($!)
    deploy_count=$((deploy_count + 1))

done < <(find "$services_subdir" -name "*.jar" -print0 2>/dev/null)

echo "[deploy] services in flight: $deploy_count"

# 等待所有后台 Docker 部署完成
wait

echo "[deploy] all done"
