#!/bin/bash

# ============================================================
# 公共构建逻辑, 被 parent / basics / services 三个入口调用
# 对应 bat 版: maozi-cloud-bat/maozi-cloud-build-jar-utils.bat
# ------------------------------------------------------------
# 参数: $1 = current_directory (调用方所在脚本目录, 如 .../maozi-cloud-shell/maozi-cloud-xxx-build)
# 运行前 cwd 已由调用方切换到实际源码工程目录。
# ------------------------------------------------------------
# 关键变量:
#   array_index              变更文件计数
#   array[1..N]              变更文件相对路径
#   buildFiles               mvn -pl 模块列表, 逗号分隔; "." 表示全量构建
#   current_build_directory  当前源码工程名, 由 scan-file-utils 设置
# ------------------------------------------------------------
# 工作流程:
#   A. 调用 scan-file-utils 收集需要构建的模块
#        分支切换或首次构建 -> buildFiles 直接置为全量
#        同分支有新提交     -> 把变更文件写入 array, 由本脚本归并
#   B. 把 array 中的变更文件归并为 -pl 模块列表 buildFiles
#   C. 执行 mvn clean + mvn install -pl buildFiles -amd
#   D. 非 parent 工程: 查找 jar, 匹配 image 目录, 生成并执行 Docker 构建脚本
# ------------------------------------------------------------
# 说明: 与 bat 版一致, scan-file-utils 通过 source 执行以共享 buildFiles / array / array_index
# ============================================================

# 调用方传入的脚本目录, 作为相对路径锚点
current_directory="$1"

# 初始化变量
array_index=0
declare -a array=()
buildFiles=""

# ---- A. 扫描变更 ----
# 当前目录不是 git 仓库 (聚合目录): 遍历每个子目录分别扫描
# 否则 (是 git 仓库): 直接扫描当前目录
if [ ! -d ".git" ]; then

    shopt -s nullglob
    for dir in */ ; do
        dir="${dir%/}"
        [ -d "$dir" ] || continue
        cd "$dir" || continue
        source "$current_directory/../maozi-cloud-scan-file-utils.sh"
        cd ..
    done
    shopt -u nullglob

else

    source "$current_directory/../maozi-cloud-scan-file-utils.sh"

fi

# ---- B. 把变更文件归并为模块列表 ----
# 仅当存在增量变更时处理; 全量构建时 buildFiles 已由 scan 直接给出
if [ "$array_index" -ne 0 ]; then

    for (( N=1; N<=array_index; N++ )); do

        file="${array[$N]}"

        # 路径以 maozi-cloud 开头: 属于某业务模块, 定位到该模块目录
        if [ "${file:0:11}" = "maozi-cloud" ]; then

            idx=0

            # 查找路径中首个 src 的位置, 截取到 src 之前即模块路径
            for (( A=1; A<=100; A++ )); do
                pathName="${file:$A:3}"
                if [ "$pathName" = "src" ]; then
                    if [ "$idx" = "0" ]; then
                        idx=$A
                        file="${file:0:$A}"
                    fi
                fi
            done

            # 未找到 src: 视为模块根文件如 pom.xml, 去掉文件名只保留目录
            if [ "$idx" = "0" ]; then
                file="${file//\\//}"
                fname="$(basename "$file")"
                file="${file%"$fname"}"
                file="${file%/}"
            fi

            # 去重后追加到 buildFiles
            if ! echo "$buildFiles" | grep -qF "$file"; then
                buildFiles="${buildFiles},${file}"
            fi

        else

            # 非 maozi-cloud 前缀的变更, 如仓库根 pom.xml
            if [ ! -d ".git" ]; then
                # 聚合目录: 取第一段路径作为模块名
                file="${file%%/*}"
                if ! echo "$buildFiles" | grep -qF "$file"; then
                    buildFiles="${buildFiles},${file}"
                fi
            else
                # git 仓库根文件变更: 直接全量构建整个 reactor
                buildFiles="."
                break
            fi

        fi

    done

fi

# ---- C. 执行 Maven 构建 ----
if [ -n "$buildFiles" ]; then

    # 清理整个 reactor; -T 16C 表示按 16 核并行
    mvn clean -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true

    # 增量安装指定模块; -amd 同时构建依赖于它们的下游模块
    mvn install -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true -pl "$buildFiles" -amd

    # 重新取当前目录名作为工程名 (聚合场景下 scan 循环会把它改成子模块名, 需还原为聚合根名)
    # 等价 bat 版: for %%A in ("%cd%") do set "current_build_directory=%%~nxA"
    current_build_directory="$(basename "$(pwd)")"

    # ---- D. 生成并执行 Docker 构建脚本, parent 工程跳过 ----
    if [ "$current_build_directory" != "maozi-cloud-parent" ]; then

        # 递归查找所有 jar, 定位其所属模块; jar 的上级目录即模块名 service_name
        while IFS= read -r -d '' jarfile; do

            # dirname 两次: jar -> target -> 模块目录
            module_dir="$(dirname "$(dirname "$jarfile")")"
            service_name="$(basename "$module_dir")"

            # image / docker 目录, 位于 maozi-cloud-script 下, 本脚本目录上三级
            image_directory="$current_directory/../../../maozi-cloud-image/${current_build_directory}-image"
            docker_directory="$current_directory/../../../maozi-cloud-docker/${current_build_directory}-docker"

            # image 目录下存在 service_name-image 条目才处理
            if [ -e "$image_directory/${service_name}-image" ]; then

                # 动态生成 service_name-build-docker.sh: 拷贝 jar, 构建镜像, compose 启动, 清理自身
                build_script="$image_directory/${service_name}-build-docker.sh"
                {
                    echo "#!/bin/bash"
                    echo "cp \"$module_dir/target/${service_name}.jar\" \"$image_directory/\""
                    echo "cd \"$image_directory\""
                    echo "docker buildx build -f \"$image_directory/${service_name}-image\" -t \"${service_name}:laster\" ."
                    echo "docker-compose -f \"$docker_directory/docker-compose.yml\" up -d ${service_name}"
                    echo "rm -f \"$image_directory/${service_name}.jar\""
                    echo "rm -f \"\$0\""
                } > "$build_script"
                chmod +x "$build_script"

                # 后台执行该 Docker 构建脚本
                bash "$build_script" &

            fi

        done < <(find . -name "*.jar" -print0)

    fi

fi
