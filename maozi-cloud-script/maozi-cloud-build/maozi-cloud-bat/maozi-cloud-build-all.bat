@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM 自动部署入口 (单一 git 仓库版)
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-build-all-distributed.sh
REM ------------------------------------------------------------
REM 项目原先拆分为 parent / basics / services 三个独立 git 仓库,
REM 现已合并为单一 git 仓库 (maozi-cloud-parent), 本脚本作为唯一入口:
REM   1. 切换到 maozi-cloud-parent 仓库根目录
REM   2. 调用 maozi-cloud-build-jar-utils.bat 完成状态比对 / Maven 构建 / Docker 部署
REM ------------------------------------------------------------
REM 构建判定逻辑:
REM   - 分支与上次记录不一致 -> 全量构建 + 全量 Docker 部署
REM   - 分支一致 SHA 不一致  -> git diff 取变更文件, 按模块 -pl -amd 增量构建,
REM                             仅对实际生成 jar 的服务做 Docker 重启
REM   - 分支与 SHA 均一致    -> 跳过
REM ============================================================

REM 切换到本脚本所在目录, 使后续相对路径 (jar-utils / scan-utils) 可靠
cd /d "%~dp0"
set "current_directory=%cd%"

REM 源码仓库根目录 (与原 maozi-cloud-parent-directory 一致)
REM 如需迁移部署路径, 改这一行即可
set "repo_directory=C:\Users\maozi\maozi-cloud\maozi-cloud-parent"

REM 切换到仓库根目录, 后续 git / mvn 命令均在此执行
cd /d "%repo_directory%"

REM 调用统一构建逻辑, 把脚本目录作为环境变量传入, 用于定位 scan-file-utils 与 image / docker 目录
call "%current_directory%\maozi-cloud-build-jar-utils.bat"

endlocal
