#!/bin/bash

# ============================================================
# 业务服务构建入口
# 对应 bat 版: maozi-cloud-bat/maozi-cloud-services-build/maozi-cloud-services-build.bat
# ------------------------------------------------------------
# 流程: 记录脚本目录 -> 读取目标源码路径 -> 切换到源码目录 -> 调用 jar-utils 执行构建
# 目标源码路径存放在同目录 maozi-cloud-services-directory 文件中
# ============================================================

# 记录当前脚本所在目录, 后续作为相对路径锚点传给 jar-utils
current_directory="$(pwd)"

# 读取要构建的源码工程绝对路径; tr -d '\r' 兼容 Windows 回车
maozi_cloud_services_directory="$(tr -d '\r' < maozi-cloud-services-directory)"

# 切换到源码工程目录, 后续 git / mvn 命令均在此执行
cd "$maozi_cloud_services_directory"

# 调用公共构建逻辑, 把脚本目录作为参数传入, 用于定位 scan-file-utils 与 image / docker 目录
bash "$current_directory/../maozi-cloud-build-jar-utils.sh" "$current_directory"
