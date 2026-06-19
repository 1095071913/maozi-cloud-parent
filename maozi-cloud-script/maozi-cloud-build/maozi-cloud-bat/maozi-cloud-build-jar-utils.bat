@echo off

REM ============================================================
REM 公共构建逻辑, 被 parent / basics / services 三个入口 call 调用
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-build-jar-utils.sh
REM ------------------------------------------------------------
REM 前置条件:
REM   1. 调用方已 cd 到实际源码工程目录
REM   2. current_directory 已由调用方设为脚本目录, 作为相对路径锚点
REM   3. 调用方已开启 setlocal enabledelayedexpansion, 故此处可用 !var!
REM ------------------------------------------------------------
REM 关键变量:
REM   array_index              变更文件计数
REM   array[1..N]              变更文件相对路径
REM   buildFiles               mvn -pl 模块列表, 逗号分隔; "." 表示全量构建
REM   current_build_directory  当前源码工程名, 如 maozi-cloud-services
REM ------------------------------------------------------------
REM 工作流程:
REM   A. 调用 scan-file-utils 收集需要构建的模块
REM        分支切换或首次构建 -> buildFiles 直接置为全量
REM        同分支有新提交     -> 把变更文件写入 array, 由本脚本归并
REM   B. 把 array 中的变更文件归并为 -pl 模块列表 buildFiles
REM   C. 执行 mvn clean + mvn install -pl buildFiles -amd
REM   D. 非 parent 工程: 查找 jar, 匹配 image 目录, 生成并执行 Docker 构建脚本
REM ============================================================

REM 初始化变量
set array_index=0

set array[0]=

set buildFiles=

REM ---- A. 扫描变更 ----
REM 当前目录不是 git 仓库, 即多仓库聚合容器: 进入每个子目录分别扫描
if not exist ".git" (

  for /d %%i in ("%cd%\*") do (

      cd %%~nxi

      call !current_directory!\..\maozi-cloud-scan-file-utils.bat !array! !buildFiles! !array_index!

      cd ../

  )

) else (
REM 当前目录本身是 git 仓库: 直接扫描
  call !current_directory!\..\maozi-cloud-scan-file-utils.bat !array! !buildFiles! !array_index!
)

REM ---- B. 把变更文件归并为模块列表 ----
REM 仅当存在增量变更时处理; 全量构建时 buildFiles 已由 scan 直接给出
if !array_index! NEQ 0 (

  for /l %%N in (1,1,!array_index!) do (

    set "file=!array[%%N]!"

    REM 路径以 maozi-cloud 开头: 属于某业务模块, 定位到该模块目录
    if "!file:~0,11!" EQU "maozi-cloud" (

      set "idx=0"

      REM 查找路径中首个 src 的位置, 截取到 src 之前即模块路径
      for /L %%A in (1,1,100) do (

        set "pathName=!file:~%%A,3!"

        if "!pathName!" EQU "src" (

          if "!idx!"=="0" (

            set "idx=%%A"

            set "file=!file:~0,%%A!"

          )

        )

      )

      REM 未找到 src: 视为模块根文件如 pom.xml, 去掉文件名只保留目录
      if "!idx!"=="0" (

        set "file=!file:\=/!"

        for %%F in ("!file!") do (
          set "file=!file:%%~nxF=!"
        )

      )

      REM 去重后追加到 buildFiles
      echo !buildFiles! | findstr /C:"!file!" > nul
      if !errorlevel! neq 0 (
        set "buildFiles=!buildFiles!,!file!"
      )

    ) else (

      REM 非 maozi-cloud 前缀的变更, 如仓库根 pom.xml
      if not exist ".git" (

        REM 聚合目录: 取第一段路径作为模块名
        for /f "delims=/" %%a in ("!file!") do set "file=%%a"

        echo !buildFiles! | findstr /C:"!file!" > nul
        if !errorlevel! neq 0 (
          set "buildFiles=!buildFiles!,!file!"
        )

      ) else (

        REM git 仓库根文件变更: 直接全量构建整个 reactor
        set "buildFiles=."

        goto :exitloop

      )

    )

  )

)

:exitloop
REM ---- C. 执行 Maven 构建 ----
if not "!buildFiles!"=="" (

  REM 清理整个 reactor; -T 8C 表示按 8 核并行
  start /B /wait cmd /c mvn clean -T 8C -Dmaven.compile.fork=true -Dmaven.test.skip=true

  REM 增量安装指定模块; -amd 同时构建依赖于它们的下游模块
  start /B /wait cmd /c mvn install -T 8C -Dmaven.compile.fork=true -Dmaven.test.skip=true -pl !buildFiles! -amd

  REM ---- D. 生成并执行 Docker 构建脚本, parent 工程跳过 ----
  for %%A in ("%cd%") do set "current_build_directory=%%~nxA"

  if not "!current_build_directory!"=="maozi-cloud-parent" (

    REM 递归查找所有 jar, 定位其所属模块; jar 上级目录即模块名 service_name
    for /r %%F in (*.jar) do (

        REM %%~dpF 为 jar 所在盘符加路径并以 \ 结尾, 截掉末尾 8 字符 \target\ 得到模块目录
        set "file_path=%%~dpF"

        set "file_path=!file_path:~0,-8!"

        for %%I in ("!file_path!") do set "service_name=%%~nxI"

        REM image / docker 目录, 位于 maozi-cloud-script 下, 本脚本目录上三级
        set "image_directory=!current_directory!\..\..\..\maozi-cloud-image\!current_build_directory!-image"

        set "docker_directory=!current_directory!\..\..\..\maozi-cloud-docker\!current_build_directory!-docker"

        REM 在 image 目录查找名为 service_name-image 的条目, 命中则生成 Docker 构建脚本
        for %%F in ("!image_directory!\*") do (

          if "!service_name!-image"=="%%~nxF" (

            REM 动态生成 service_name-build-docker.bat: 拷贝 jar, 构建镜像, compose 启动, 清理自身
            echo copy "!file_path!\target\!service_name!.jar" "!image_directory!\" >> !image_directory!/!service_name!-build-docker.bat

            echo cd !image_directory!\ >> !image_directory!/!service_name!-build-docker.bat

            echo docker buildx build -f !image_directory!\!service_name!-image -t !service_name!:laster . >> !image_directory!/!service_name!-build-docker.bat

            echo docker-compose -f !docker_directory!\docker-compose.yml up -d !service_name! >> !image_directory!/!service_name!-build-docker.bat

            echo del "!image_directory!\!service_name!.jar" >> !image_directory!/!service_name!-build-docker.bat

            echo del "!image_directory!\!service_name!-build-docker.bat" >> !image_directory!/!service_name!-build-docker.bat

            start /B cmd /c !image_directory!\!service_name!-build-docker.bat

          )

        )

    )

  )

)
