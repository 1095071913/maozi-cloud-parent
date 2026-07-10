@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM 强制全量部署入口 (单一 git 仓库版)
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-build-all-distributed-force.sh
REM ------------------------------------------------------------
REM 与 maozi-cloud-build-all.bat 的区别:
REM   - 不读取 / 写入 maozi-cloud-parent-env 状态 (分支 / SHA)
REM   - 不做 git diff 增量比对
REM   - 不做 jar mtime 前后快照对比
REM   - 无条件: 全量 mvn 构建 + 所有在 JSON 配置里登记的服务全部重建并重启
REM ------------------------------------------------------------
REM 工作流程:
REM   1. 切换到仓库根目录
REM   2. 构建基础镜像 maozi-cloud-base-jdk:1.0.0 (服务镜像 FROM 它, 必须先就绪)
REM   3. mvn clean install -T 16C 全量构建整个 reactor
REM   4. 扫描 maozi-cloud-services 下所有 jar
REM   5. 对每个 jar:
REM        - call maozi-cloud-render-image.bat: 读 maozi-cloud-services.json,
REM          按模板渲染 !service_name!-image, 生成 !service_name!-build-docker.bat
REM        - 服务不在 JSON 配置里时, 渲染器返回非零并 skip, 不阻塞其他服务
REM        - 后台 start 渲染器生成的 build-docker.bat
REM   6. 轮询等待所有后台部署完成
REM ------------------------------------------------------------
REM 适用场景:
REM   - 首次部署
REM   - 切换分支 / 合并代码后想强制重新部署一切
REM   - base-jdk 镜像或 OTel agent 变更后需要全部重建
REM   - 怀疑增量部署有遗漏, 想做一次彻底的回归
REM ============================================================

REM 切换到本脚本所在目录, 使后续相对路径可靠
cd /d "%~dp0"
set "current_directory=%cd%"

REM 源码仓库根目录 (与 maozi-cloud-build-all.bat 保持一致)
REM 如需迁移部署路径, 改这一行即可
set "repo_directory=C:\Users\maozi\maozi-cloud\maozi-cloud-parent"

REM 切换到仓库根目录, 后续 mvn 命令均在此执行
cd /d "%repo_directory%"

REM 可部署服务源码根目录 (仅此目录下生成的 jar 才会触发 Docker 部署)
set "services_subdir=maozi-cloud-service\maozi-cloud-services"

REM 锁定仓库根的绝对路径, 让生成的临时脚本里的 copy 源路径不依赖 cwd
set "repo_root=%cd%"

REM ============================================================
REM 1. 构建基础 JDK 镜像 (所有服务镜像 FROM 它 / business-jdk)
REM ------------------------------------------------------------
REM 两层结构, 必须按顺序构建:
REM   a) maozi-cloud-base-jdk:1.0.0     OS + 时区 + dumb-init (不含 OTel)
REM   b) maozi-cloud-business-jdk:1.0.0 FROM base-jdk + OTel Agent jar
REM 服务镜像按 OTel 开关 FROM 其中一个, 详见 maozi-cloud-services.json
REM ============================================================
set "base_image_directory=%current_directory%\..\..\maozi-cloud-image\maozi-cloud-base-jdk-image"
set "business_image_directory=%current_directory%\..\..\maozi-cloud-image\maozi-cloud-business-jdk-image"

if exist "%base_image_directory%\Dockerfile" (
    echo [base] building maozi-cloud-base-jdk:1.0.0
    pushd "%base_image_directory%"
    call docker buildx build -f Dockerfile -t maozi-cloud-base-jdk:1.0.0 .
    if !errorlevel! neq 0 (
        popd
        echo [base] FAILED: maozi-cloud-base-jdk:1.0.0 build error, abort
        exit /b 1
    )
    popd
    echo [base] done
) else (
    echo [base] skip: Dockerfile not found at %base_image_directory%\Dockerfile
)

if exist "%business_image_directory%\Dockerfile" (
    echo [base] building maozi-cloud-business-jdk:1.0.0
    REM business-jdk FROM base-jdk, 必须等上一步 base-jdk 构建完才能 FROM 到
    REM 上下文为 Dockerfile 所在目录 (含 OTel agent jar)
    pushd "%business_image_directory%"
    call docker buildx build -f Dockerfile -t maozi-cloud-business-jdk:1.0.0 .
    if !errorlevel! neq 0 (
        popd
        echo [base] FAILED: maozi-cloud-business-jdk:1.0.0 build error, abort
        exit /b 1
    )
    popd
    echo [base] done
) else (
    echo [base] skip: Dockerfile not found at %business_image_directory%\Dockerfile
)

REM ============================================================
REM 2. 全量 Maven 构建 (不做 -pl / -amd 增量, 直接整个 reactor)
REM ============================================================
echo [build] mvn clean install (full reactor)

REM 复用 jar-utils 的参数:
REM   -T 16C                    并行线程数 = 16 * CPU 核
REM   -Dmaven.compile.fork=true 编译过程 fork 专属 JVM, 加快速度
REM   -Dmaven.test.skip=true    跳过测试编译与执行, 加快部署
start /B /wait cmd /c mvn clean install -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true

REM Maven 失败立即中止, 不进入 Docker 部署阶段 (避免用旧 jar 重建镜像)
if !errorlevel! neq 0 (
    echo [build] FAILED: mvn clean install error, abort
    exit /b 1
)

REM ============================================================
REM 3. 扫描所有 jar, 过滤出可部署的服务
REM ============================================================
REM 用 PowerShell 把 services_subdir 下所有 jar 完整路径写入临时列表
set "all_jars_list=%TEMP%\maozi-cloud-jar-all.txt"
powershell -NoProfile -Command "Get-ChildItem -Path '%services_subdir%' -Recurse -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName } | Set-Content -Path '%all_jars_list%' -Encoding ASCII"

set "all_jars_count=0"
for /f "usebackq delims=" %%J in ("%all_jars_list%") do (
    set /a all_jars_count+=1
    set "all_jar_!all_jars_count!=%%J"
)

REM ============================================================
REM 4. 对每个 jar 触发 Docker 部署
REM ============================================================
set "deploy_count=0"

REM 镜像 / docker-compose 目录的公共父目录
set "image_base=%current_directory%\..\..\maozi-cloud-image"
set "docker_base=%current_directory%\..\..\maozi-cloud-docker"

for /l %%N in (1,1,!all_jars_count!) do (

    set "jarfile=!all_jar_%%N!"

    REM dirname 两次: jar -> target -> 模块目录
    REM business 服务: 得到 maozi-cloud-business-xxx\maozi-cloud-xxx-service (启动模块)
    REM basics 服务:   得到 maozi-cloud-gateway-service / maozi-cloud-monitor-service (扁平单模块)
    for %%I in ("!jarfile!") do set "jar_dir=%%~dpI"
    REM jar_dir 末尾带 \, 再上一级
    for %%I in ("!jar_dir!\..") do set "module_dir=%%~fI"
    for %%I in ("!module_dir!") do set "module_name=%%~nxI"
    set "module_dir=!module_dir:\=/!"
    set "service_name=!module_name!"

    REM 渲染 + 生成 build-docker.bat 统一交给 maozi-cloud-render-image.bat:
    REM   - 读 maozi-cloud-services.json 取该服务的端口 / Dubbo / OTel / JVM / base_image
    REM   - 按模板渲染出 !service_name!-image, 并生成 !service_name!-build-docker.bat
    REM 服务不在 JSON 配置里时, 渲染器返回非零并打印 skip, 不阻塞其他服务
    call "%current_directory%\maozi-cloud-render-image.bat" "!service_name!" "!module_dir!"

    if !errorlevel! equ 0 (
        REM 后台执行渲染器刚生成的 build-docker.bat (buildx + compose + 自清理)
        REM 路由结果 (basics vs services) 在渲染器里完成, 这里只取最终 image_directory
        REM gateway / monitor 已迁到 services-docker, 只有 maozi-cloud-basics-* 走 basics
        set "is_basics=0"
        echo !service_name! | findstr /B /C:"maozi-cloud-basics-" >nul
        if !errorlevel! equ 0 set "is_basics=1"
        if "!is_basics!"=="1" (
            set "build_script=!image_base!\maozi-cloud-basics-image\!service_name!-build-docker.bat"
        ) else (
            set "build_script=!image_base!\maozi-cloud-services-image\!service_name!-build-docker.bat"
        )
        start "" /B cmd /c "!build_script!"
        set /a deploy_count+=1
    )

)

echo [deploy] services in flight: !deploy_count!

REM ============================================================
REM 5. 等待所有后台 Docker 部署完成
REM ============================================================
REM 批处理没有原生 wait, 用 timeout 循环检测 build-docker.bat 是否还存在
:wait_deploy
timeout /t 1 /nobreak >nul
set "pending=0"
for %%F in ("%image_base%\maozi-cloud-basics-image\*-build-docker.bat" "%image_base%\maozi-cloud-services-image\*-build-docker.bat") do (
    if exist "%%F" set "pending=1"
)
if "!pending!"=="1" goto :wait_deploy

REM 清理空的 maozi-cloud-services-image / -basics-image 目录
REM (渲染产物由各服务动态生成的 !service_name!-build-docker.bat 自清理, 留下两个空目录, 一并 rd 掉)
REM rd 不带 /s 只删空目录, 有遗留文件时 rd 失败, 保留现场供排查
rd "%image_base%\maozi-cloud-services-image" 2>nul && echo [cleanup] removed empty maozi-cloud-services-image
rd "%image_base%\maozi-cloud-basics-image"   2>nul && echo [cleanup] removed empty maozi-cloud-basics-image

REM 清理临时文件
if exist "%all_jars_list%" del "%all_jars_list%"

echo [deploy] all done

endlocal
