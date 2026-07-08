@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM 镜像渲染器入口 (调用 maozi-cloud-render-image.ps1)
REM ------------------------------------------------------------
REM 对应 shell 版: maozi-cloud-shell/maozi-cloud-render-image.sh
REM ------------------------------------------------------------
REM 调用方式: call maozi-cloud-render-image.bat <service_name> <module_dir>
REM 依赖调用方已设: current_directory, repo_root
REM 退出码: 0 = 已渲染 + 已生成 build-docker.bat; 非 0 = skip (服务不在 JSON / 渲染失败)
REM ============================================================

set "_render_service_name=%~1"
set "_render_module_dir=%~2"

set "_render_image_root=%current_directory%\..\..\maozi-cloud-image"
set "_render_config=%_render_image_root%\maozi-cloud-services.json"
set "_render_template=%_render_image_root%\maozi-cloud-service-image.template"

REM 按服务名前缀路由镜像 / docker-compose 目录 (与 build-jar-utils.bat 一致)
set "_render_image_base=%current_directory%\..\..\maozi-cloud-image"
set "_render_docker_base=%current_directory%\..\..\maozi-cloud-docker"

set "_render_is_basics=0"
echo !_render_service_name! | findstr /B /C:"maozi-cloud-basics-" >nul
if !errorlevel! equ 0 set "_render_is_basics=1"

if "!_render_is_basics!"=="1" (
    set "_render_image_directory=%_render_image_base%\maozi-cloud-basics-image"
    set "_render_docker_directory=%_render_docker_base%\maozi-cloud-basics-docker"
) else (
    set "_render_image_directory=%_render_image_base%\maozi-cloud-services-image"
    set "_render_docker_directory=%_render_docker_base%\maozi-cloud-services-docker"
)
set "_render_image_file=!_render_image_directory!\!_render_service_name!-image"

powershell -NoProfile -ExecutionPolicy Bypass -File "%current_directory%\maozi-cloud-render-image.ps1" ^
    -ServiceName        "!_render_service_name!" ^
    -ConfigFile         "!_render_config!" ^
    -TemplateFile       "!_render_template!" ^
    -ImageFile          "!_render_image_file!" ^
    -ImageDirectory     "!_render_image_directory!" ^
    -DockerDirectory    "!_render_docker_directory!" ^
    -RepoRoot           "!repo_root!" ^
    -ModuleDir          "!_render_module_dir!"

exit /b !errorlevel!
