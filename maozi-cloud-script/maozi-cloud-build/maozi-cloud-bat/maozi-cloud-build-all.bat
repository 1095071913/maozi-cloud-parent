@echo off

REM ============================================================
REM 一键全量构建入口
REM 依次构建: parent 父工程 -> basics 基础服务 -> services 业务服务
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-build-all.sh
REM ------------------------------------------------------------
REM parent   使用 start /B /wait  同步阻塞, 必须先完成, basics 与 services 均依赖它
REM basics   使用 start /B        后台异步执行
REM services 使用 start /B        后台异步执行
REM ============================================================

REM 1. 构建父工程: 同步等待其完成
cd maozi-cloud-parent-build

start /B /wait cmd /c maozi-cloud-parent-build.bat

REM 2. 构建基础服务: 后台执行
cd ../maozi-cloud-basics-build

start /B cmd /c maozi-cloud-basics-build.bat

REM 3. 构建业务服务: 后台执行
cd ../maozi-cloud-services-build

start /B cmd /c maozi-cloud-services-build.bat

exit
