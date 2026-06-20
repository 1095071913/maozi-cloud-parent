@echo off

REM ============================================================
REM 变更扫描工具 (单一 git 仓库版), 由 jar-utils 通过 call 调用
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-scan-file-utils.sh
REM ------------------------------------------------------------
REM 作用: 在 maozi-cloud-parent 仓库根目录比对分支与提交, 判定本次构建范围
REM   通过脚本目录下的 maozi-cloud-parent-env 文件夹记录上次构建状态:
REM     CURRENT_BRANCH  上次构建时的分支
REM     CURRENT_SHA     上次构建时的提交 SHA
REM   并据此输出给 jar-utils:
REM     build_mode      full / incremental / none
REM     changed_files_N 增量模式下的变更文件 (相对仓库根的路径), N 从 1 开始
REM     changed_files_count 变更文件总数
REM ------------------------------------------------------------
REM 四种判定:
REM   A. 无 CURRENT_BRANCH 记录: 首次构建, 全量
REM   B. 分支与记录不一致:       切换了分支, 全量
REM   C. 分支一致但 SHA 变化:    同分支有新提交, 增量
REM   D. 分支与 SHA 均一致:      无变化, 跳过
REM ------------------------------------------------------------
REM 依赖外部变量: current_directory  脚本目录
REM 前置: cwd 已切换到 maozi-cloud-parent 仓库根目录
REM 修改外部变量: build_mode, changed_files_count, changed_files_*
REM ============================================================

REM 重置输出变量
set "build_mode=none"
set "changed_files_count=0"

REM 读取当前 git 分支与 HEAD SHA
for /f "tokens=*" %%i in ('git branch --show-current') do set "current_branch=%%i"
for /f "delims=" %%a in ('git rev-parse HEAD') do set "current_sha=%%a"

REM env 状态目录: 统一存放 maozi-cloud-parent 仓库的构建状态
set "env_directory=%current_directory%\maozi-cloud-parent-env"

if not exist "%env_directory%" mkdir "%env_directory%"

set "branch_file=%env_directory%\CURRENT_BRANCH"
set "sha_file=%env_directory%\CURRENT_SHA"

if exist "%branch_file%" (

    REM 读取上次记录的分支并去除空格
    set /p file_branch=<"%branch_file%"
    set "file_branch=!file_branch: =!"

    REM ---- B. 分支不一致: 切换了分支, 全量构建 ----
    if not "!current_branch!"=="!file_branch!" (

        set "build_mode=full"

    ) else (

        REM ---- C. 分支一致: 比较 SHA ----
        set /p file_sha=<"%sha_file%"
        set "file_sha=!file_sha: =!"

        if not "!current_sha!"=="!file_sha!" (

            REM 有新提交: 增量构建
            set "build_mode=incremental"

            REM 用 git diff 列出变更文件 (相对仓库根), 写入 changed_files_* 数组
            for /f "tokens=* delims=" %%A in ('git diff --name-only !file_sha!..!current_sha!') do (
                set /a changed_files_count+=1
                set "changed_files_!changed_files_count!=%%A"
            )

        )

    )

) else (

    REM ---- A. 无记录: 首次构建, 全量 ----
    set "build_mode=full"

)

REM 记录本次构建的状态 (无论后续 mvn 是否成功都更新到当前)
echo !current_branch!> "%branch_file%"
echo !current_sha!> "%sha_file%"
