@echo off

REM ============================================================
REM 变更扫描工具, 由 jar-utils 通过 call 调用
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-scan-file-utils.sh
REM ------------------------------------------------------------
REM 作用: 基于分支与提交增量判断需要构建的范围
REM   通过脚本目录下的 {工程名}-env 文件夹记录上次构建状态:
REM     CURRENT_BRANCH  上次构建时的分支
REM     CURRENT_SHA     上次构建时的提交 SHA
REM   并据此输出两种结果之一给 jar-utils:
REM     1. 全量构建 -> 直接设置 buildFiles 为 "." 或模块名
REM     2. 增量构建 -> 把变更文件路径写入 array, 由 jar-utils 归并
REM ------------------------------------------------------------
REM 三种判定:
REM   A. 无 CURRENT_BRANCH 记录: 首次构建, 全量
REM   B. 分支与记录不一致:       切换了分支, 全量
REM   C. 分支一致但 SHA 变化:    同分支有新提交, 增量
REM ------------------------------------------------------------
REM 依赖外部变量: current_directory  脚本目录
REM 修改外部变量: buildFiles, array_index, array[], current_build_directory
REM ============================================================

REM 取当前源码工程目录名, 如 maozi-cloud-services
set "current_build_directory=%cd%"
for %%A in ("%current_build_directory%") do set "current_build_directory=%%~nxA"

REM env 状态目录名: {工程名}-env, 存放分支与 SHA 记录
set "current_env_build_directory=!current_build_directory!-env"

REM 读取当前 git 分支
for /f "tokens=*" %%i in ('git branch --show-current') do (
  set current_branch=%%i
)

REM 确保状态目录存在
if not exist "!current_directory!\!current_env_build_directory!" (
  mkdir !current_directory!\!current_env_build_directory!
)

if exist !current_directory!\!current_env_build_directory!\CURRENT_BRANCH (

    REM 读取上次记录的分支并去除空格
    set /p file_branch=<!current_directory!/!current_env_build_directory!/CURRENT_BRANCH

    set file_branch=!file_branch: =!

    REM ---- B. 分支不一致: 切换了分支, 全量构建 ----
    if not "!current_branch!"=="!file_branch!" (

      echo %current_branch% > !current_directory!/!current_env_build_directory!/CURRENT_BRANCH

      for /f "delims=" %%a in ('git rev-parse HEAD') do echo %%a > !current_directory!/!current_env_build_directory!/CURRENT_SHA

      REM 上级有 pom.xml 说明本目录是聚合下的子模块, 构建该子模块; 否则全量 reactor
      if exist "..\pom.xml" (
        set "buildFiles=!buildFiles!,!current_build_directory!"
      ) else (
        set "buildFiles=."
      )

    ) else (

      REM ---- C. 分支一致: 比较 SHA 判断是否有新提交 ----
      for /f "delims=" %%a in ('git rev-parse HEAD') do set current_sha=%%a

      set /p file_sha=<!current_directory!/!current_env_build_directory!/CURRENT_SHA

      set file_sha=!file_sha: =!

      if not "!current_sha!"=="!file_sha!" (

        REM 有新提交: 更新 SHA, 用 git diff 列出变更文件写入 array
        echo !current_sha! > !current_directory!/!current_env_build_directory!/CURRENT_SHA

        for /F "tokens=*" %%A in ('git diff --name-only !file_sha!..!current_sha!') do (

            set /a array_index+=1

            REM 子模块追加模块名前缀; 仓库根则直接用相对路径
            if exist "..\pom.xml" (
                set "array[!array_index!]=!current_build_directory!/%%A"
            ) else (
                set "array[!array_index!]=%%A"
            )

        )

      )

    )

) else (

  REM ---- A. 无记录: 首次构建, 全量 ----
  echo !current_branch! > !current_directory!/!current_env_build_directory!/CURRENT_BRANCH

  for /f "delims=" %%a in ('git rev-parse HEAD') do echo %%a > !current_directory!/!current_env_build_directory!/CURRENT_SHA

  if exist "..\pom.xml" (
    set "buildFiles=!buildFiles!,!current_build_directory!"
  ) else (
    set "buildFiles=."
  )

)
