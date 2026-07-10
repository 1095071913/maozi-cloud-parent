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
REM        - call maozi-cloud-render-image.bat: 读 maozi-cloud-services.json,
REM          按模板渲染 !service_name!-image, 生成 !service_name!-build-docker.bat
REM        - 服务不在 JSON 配置里时, 渲染器返回非零并 skip, 不阻塞其他服务
REM        - 后台 start 渲染器生成的 build-docker.bat, 等待全部完成
REM ------------------------------------------------------------
REM 模块结构说明 (business 服务):
REM   业务服务采用聚合分层, 一个业务在 maozi-cloud-services 下聚合为:
REM     maozi-cloud-business-xxx\
REM       ├── maozi-cloud-xxx-api       对外 API 契约 (产出带版本 jar, 不部署)
REM       ├── maozi-cloud-xxx-business  业务实现 (产出带版本 jar, 不部署)
REM       └── maozi-cloud-xxx-service   可执行启动模块 (产出 maozi-cloud-xxx-service.jar, 部署入口)
REM   find 会扫描到上述全部 jar, 但只有 *-service 模块在 maozi-cloud-services.json 里登记,
REM   api / business 的 jar 因不在 JSON 里会在 F 步被渲染器自动跳过.
REM   basics 层 (gateway-service / monitor-service) 与 all-service 仍为扁平单模块, 行为不变。
REM ============================================================

REM 可部署服务源码根目录 (相对仓库根), 仅此目录下生成的 jar 才触发 Docker 重新部署
set "services_subdir=maozi-cloud-service\maozi-cloud-services"

REM 锁定仓库根的绝对路径, 让生成的临时脚本里的 copy 源路径不依赖 cwd
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
REM image_base 在末尾 wait 循环里用于扫描 *-build-docker.bat, 在这里先设好
set "image_base=%current_directory%\..\..\maozi-cloud-image"

for /l %%N in (1,1,!changed_jars_count!) do (

    set "jarfile=!changed_jar_%%N!"

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

REM 清理空的 maozi-cloud-services-image / -basics-image 目录
REM (渲染产物由各服务动态生成的 !service_name!-build-docker.bat 自清理, 留下两个空目录, 一并 rd 掉)
REM rd 不带 /s 只删空目录, 有遗留文件时 rd 失败, 保留现场供排查
rd "%image_base%\maozi-cloud-services-image" 2>nul && echo [cleanup] removed empty maozi-cloud-services-image
rd "%image_base%\maozi-cloud-basics-image"   2>nul && echo [cleanup] removed empty maozi-cloud-basics-image

REM 清理临时文件
if exist "%before_manifest%" del "%before_manifest%"
if exist "%after_manifest%" del "%after_manifest%"
if exist "%changed_jars_list%" del "%changed_jars_list%"

endlocal
