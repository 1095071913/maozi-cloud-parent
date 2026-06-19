@echo off

setlocal enabledelayedexpansion

REM ============================================================
REM 业务服务构建入口
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-services-build/maozi-cloud-services-build.sh
REM ------------------------------------------------------------
REM 流程: 记录脚本目录 -> 读取目标源码路径 -> 切换到源码目录 -> 调用 jar-utils 执行构建
REM 目标源码路径存放在同目录 maozi-cloud-services-directory 文件中
REM ============================================================

REM 记录当前脚本所在目录, 后续作为相对路径锚点传给 jar-utils
set "current_directory=%cd%"

REM 读取要构建的源码工程绝对路径
set /p maozi_cloud_services_directory=<maozi-cloud-services-directory

REM 切换到源码工程目录, 后续 git / mvn 命令均在此执行
cd !maozi_cloud_services_directory!

REM 调用公共构建逻辑, 把脚本目录作为参数传入, 用于定位 scan-file-utils 与 image / docker 目录
call !current_directory!\..\maozi-cloud-build-jar-utils.bat !current_directory!

endlocal

exit
