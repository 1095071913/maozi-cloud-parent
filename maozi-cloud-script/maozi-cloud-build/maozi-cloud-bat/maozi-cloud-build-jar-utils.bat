@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM 统一构建逻辑 (单一 git 仓库版), 由 maozi-cloud-build-all.bat 调用
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-build-jar-utils.sh
REM ------------------------------------------------------------
REM 前置条件:
REM   1. 调用方已 cd 到 maozi-cloud-parent 仓库根目录
REM   2. current_directory 已由调用方设为脚本目录, 作为相对路径锚点
REM   3. 调用方已开启 setlocal enabledelayedexpansion, 故此处可用 !var!
REM ------------------------------------------------------------
REM 工作流程:
REM   A. call scan-file-utils 比对 git 分支 / SHA, 得到 build_mode
REM        full          -> 全量构建整个 reactor
REM        incremental   -> 按 changed_files_* 归并出 Maven 模块列表
REM        none          -> 无变化, 直接退出
REM   B. 拍摄 maozi-cloud-services 下所有 jar 的 mtime 快照 (构建前)
REM   C. 归并 changed_files_* 为 mvn -pl 模块路径列表
REM        - 源文件: 截取 \src\ 之前的部分作为模块路径
REM        - pom.xml: 取所在目录, 仓库根 pom 触发全量
REM        - 其他非 Maven 文件: 忽略
REM   D. mvn clean + mvn install -pl <modules> -amd
REM        -amd 自动让 "a 引用 b, b 修改 -> a 一起构建" 成立
REM   E. 拍摄构建后的 jar mtime 快照, 找出 mtime 变化 (或新增) 的 jar
REM        只有这些 jar 才视为 "被 Maven 实际编译并生成", 进入 Docker 部署
REM   F. 对每个变化的 jar:
REM        - 按模块名前缀路由镜像目录 (basics-* -> maozi-cloud-basics-image 等)
REM        - 校验镜像 Dockerfile 存在
REM        - 生成临时 build-docker.bat: 拷贝 jar / buildx / compose up -d / 自清理
REM        - 并行启动, 等待全部完成
REM ------------------------------------------------------------
REM 模块结构说明 (business 服务):
REM   业务服务采用聚合分层, 一个业务在 maozi-cloud-services 下聚合为:
REM     maozi-cloud-business-xxx\
REM       ├── maozi-cloud-xxx-api       对外 API 契约 (产出带版本 jar, 不部署)
REM       ├── maozi-cloud-xxx-service   业务实现 (产出带版本 jar, 不部署)
REM       └── maozi-cloud-xxx-run       可执行启动模块 (产出 maozi-cloud-xxx-run.jar, 部署入口)
REM   find 会扫描到上述全部 jar, 但只有 *-run 模块在镜像目录存在对应的
REM   *-run-image Dockerfile, api / service 的 jar 会在 F 步镜像校验时被自动跳过。
REM   basics 层 (gateway / monitor) 与 all-service 仍为扁平单模块, 行为不变。
REM ============================================================

REM 可部署服务源码根目录 (相对仓库根), 仅此目录下生成的 jar 才触发 Docker 重新部署
set "services_subdir=maozi-cloud-service\maozi-cloud-services"

REM 锁定仓库根的绝对路径, 让生成的临时脚本里的 cp 源路径不依赖 cwd
set "repo_root=%cd%"

REM ---- A. 比对 git 状态 ----
call "%current_directory%\maozi-cloud-scan-file-utils.bat"

echo [build] mode=!build_mode!

REM 无变更: 直接退出, 不触发 Maven 与 Docker
if "!build_mode!"=="none" goto :eof

REM ---- B. 拍摄构建前 jar mtime 快照 ----
REM 用 PowerShell 把 services_subdir 下所有 jar 的 "完整路径|LastWriteTimeTicks" 写入临时文件
set "before_manifest=%TEMP%\maozi-cloud-jar-before.txt"
powershell -NoProfile -Command "Get-ChildItem -Path '%services_subdir%' -Recurse -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { '{0}|{1}' -f $_.FullName, $_.LastWriteTime.Ticks } | Set-Content -Path '%before_manifest%' -Encoding ASCII"

REM ---- C. 归并 changed_files_* 为 Maven 模块路径列表 ----
set "build_files="

if "!build_mode!"=="full" (

    REM 全量构建整个 reactor
    set "build_files=."

) else if "!build_files!"=="" (

    REM 增量构建: 把变更文件归并为模块路径, 通过 changed_modules_*=1 做去重
    set "changed_modules_count=0"

    for /l %%N in (1,1,!changed_files_count!) do (

        set "file=!changed_files_%%N!"
        set "module_path="

        REM 判断路径形态: 含 \src\ 是源文件, 是 pom.xml 则取目录, 否则忽略
        REM !file! 中 / 与 \ 混用, 先统一为 \ 便于后续 findstr 匹配
        set "file_norm=!file:/=\!"

        REM 检测 \src\ 子串
        echo !file_norm! | findstr /C:"\src\" >nul
        if !errorlevel! equ 0 (
            REM 源文件: 截取 \src\ 之前的部分作为模块路径
            for /f "delims=" %%T in ("!file_norm!") do (
                REM 用 PowerShell 截断, 避免批处理字符串切片的多字节坑
                for /f "delims=" %%P in ('powershell -NoProfile -Command "$s='!file_norm!'; $i=$s.IndexOf('\src\'); Write-Output $s.Substring(0,$i)"') do set "module_path=%%P"
            )
        ) else if "!file_norm!"=="pom.xml" (
            REM 仓库根 pom.xml 变更: 影响整个 reactor, 直接全量
            set "build_files=."
        ) else if "!file_norm:~-8!"=="\pom.xml" (
            REM 子模块 / 聚合 pom.xml 变更: 取所在目录作为模块路径
            set "module_path=!file_norm:~0,-8!"
        ) else if exist "!file_norm!\pom.xml" (
            REM 路径本身就是一个含 pom.xml 的目录, 通常是 git submodule 指针变化
            REM (git diff 只返回子模块目录路径, 不返回子模块内部文件)
            set "module_path=!file_norm!"
        )

        REM 必须真实存在 pom.xml 才算 Maven 模块, 否则忽略
        if not "!module_path!"=="" if exist "!module_path!\pom.xml" (
            REM 去重写入 changed_modules_*=1
            if not defined changed_modules_!module_path! (
                set /a changed_modules_count+=1
                set "changed_modules_!module_path!=1"
                set "changed_modules_list_!changed_modules_count!=!module_path!"
            )
        )

    )

    REM 合并模块路径为逗号分隔的 -pl 参数, build_files 已置 "." 则跳过
    if not "!build_files!"=="." (
        for /l %%N in (1,1,!changed_modules_count!) do (
            if "!build_files!"=="" (
                set "build_files=!changed_modules_list_%%N!"
            ) else (
                set "build_files=!build_files!,!changed_modules_list_%%N!"
            )
        )
    )

)

REM ---- D. 执行 Maven 构建 ----
if not "!build_files!"=="" (

    echo [build] mvn -pl !build_files! -amd

    REM 清理整个 reactor; -T 16C 表示按 16 核并行
    start /B /wait cmd /c mvn clean -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true

    REM 增量安装指定模块; -amd 同时构建依赖于它们的下游模块
    REM 这一步实现了 "a 引用 b, b 修改 -> a 也重新构建"
    start /B /wait cmd /c mvn install -T 16C -Dmaven.compile.fork=true -Dmaven.test.skip=true -pl !build_files! -amd

)

REM ---- E. 拍摄构建后 jar mtime 快照, 找出实际更新的 jar ----
REM 用 PowerShell 比对前后两份 manifest, 输出 mtime 变化或新增的 jar 完整路径
set "after_manifest=%TEMP%\maozi-cloud-jar-after.txt"
powershell -NoProfile -Command "Get-ChildItem -Path '%services_subdir%' -Recurse -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { '{0}|{1}' -f $_.FullName, $_.LastWriteTime.Ticks } | Set-Content -Path '%after_manifest%' -Encoding ASCII"

REM 用 PowerShell diff 两个 manifest, 把变化的 jar 路径写到一个列表文件
set "changed_jars_list=%TEMP%\maozi-cloud-jar-changed.txt"
powershell -NoProfile -Command "$before=@{}; Get-Content -Path '%before_manifest%' -ErrorAction SilentlyContinue | ForEach-Object { $p=$_.Split('|',2); $before[$p[0]]=$p[1] }; Get-Content -Path '%after_manifest%' -ErrorAction SilentlyContinue | ForEach-Object { $p=$_.Split('|',2); if (-not $before.ContainsKey($p[0]) -or $before[$p[0]] -ne $p[1]) { Write-Output $p[0] } } | Set-Content -Path '%changed_jars_list%' -Encoding ASCII"

REM 读取 changed_jars_list 并行部署
set "changed_jars_count=0"
for /f "usebackq delims=" %%J in ("%changed_jars_list%") do (
    set /a changed_jars_count+=1
    set "changed_jar_!changed_jars_count!=%%J"
)

echo [deploy] changed jars: !changed_jars_count!

REM ---- F. 对每个变化的 jar 触发 Docker 部署 ----
for /l %%N in (1,1,!changed_jars_count!) do (

    set "jarfile=!changed_jar_%%N!"

    REM dirname 两次: jar -> target -> 模块目录
    REM business 服务: 得到 maozi-cloud-business-xxx\maozi-cloud-xxx-run (启动模块)
    REM basics 服务:   得到 maozi-cloud-basics-xxx (扁平单模块)
    for %%I in ("!jarfile!") do set "jar_dir=%%~dpI"
    REM jar_dir 末尾带 \, 再上一级
    for %%I in ("!jar_dir!\..") do set "module_dir=%%~fI"
    for %%I in ("!module_dir!") do set "module_name=%%~nxI"
    set "module_dir=!module_dir:\=/!"
    set "service_name=!module_name!"

    REM 按模块名前缀路由镜像 / docker-compose 目录
    set "image_base=%current_directory%\..\..\maozi-cloud-image"
    set "docker_base=%current_directory%\..\..\maozi-cloud-docker"

    REM maozi-cloud-basics-* -> maozi-cloud-basics-image / -docker, 否则 services
    echo !service_name! | findstr /B /C:"maozi-cloud-basics-" >nul
    if !errorlevel! equ 0 (
        set "image_directory=!image_base!\maozi-cloud-basics-image"
        set "docker_directory=!docker_base!\maozi-cloud-basics-docker"
    ) else (
        set "image_directory=!image_base!\maozi-cloud-services-image"
        set "docker_directory=!docker_base!\maozi-cloud-services-docker"
    )

    REM 校验镜像目录下存在 {service_name}-image Dockerfile, 不存在则跳过
    if exist "!image_directory!\!service_name!-image" (

        REM 动态生成 service_name-build-docker.bat: 拷贝 jar / 构建镜像 / compose 启动 / 清理自身
        set "build_script=!image_directory!\!service_name!-build-docker.bat"

        REM 清空旧脚本
        type nul > "!build_script!"

        echo copy "!repo_root!\!module_dir!\target\!service_name!.jar" "!image_directory!\" >> "!build_script!"
        echo cd /d "!image_directory!" >> "!build_script!"
        echo docker buildx build -f "!image_directory!\!service_name!-image" -t !service_name!:laster . >> "!build_script!"
        echo docker-compose -f "!docker_directory!\docker-compose.yml" up -d !service_name! >> "!build_script!"
        echo del "!image_directory!\!service_name!.jar" >> "!build_script!"
        echo del "%%~f0" >> "!build_script!"

        echo [deploy] !service_name!: building image and starting container
        REM 后台执行该 Docker 构建脚本, 多个服务可并行部署
        start "" /B cmd /c "!build_script!"

    ) else (

        echo [deploy] skip !service_name!: image file not found at "!image_directory!\!service_name!-image"

    )

)

REM 等待所有后台 Docker 部署完成
REM 批处理没有原生 wait, 用 timeout 循环检测 build-docker.bat 是否还存在
:wait_deploy
timeout /t 1 /nobreak >nul
set "pending=0"
for %%F in ("%image_base%\maozi-cloud-basics-image\*-build-docker.bat" "%image_base%\maozi-cloud-services-image\*-build-docker.bat") do (
    if exist "%%F" set "pending=1"
)
if "!pending!"=="1" goto :wait_deploy

REM 清理临时文件
if exist "%before_manifest%" del "%before_manifest%"
if exist "%after_manifest%" del "%after_manifest%"
if exist "%changed_jars_list%" del "%changed_jars_list%"

endlocal
