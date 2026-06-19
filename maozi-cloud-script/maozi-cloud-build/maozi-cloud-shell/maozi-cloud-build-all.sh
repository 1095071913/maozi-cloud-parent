#!/bin/bash

# ============================================================
# 一键全量构建入口
# 依次构建: parent 父工程 -> basics 基础服务 -> services 业务服务
# 对应 bat 版: maozi-cloud-bat/maozi-cloud-build-all.bat
# ------------------------------------------------------------
# parent   同步阻塞执行, 必须先完成, basics 与 services 均依赖它
# basics   后台异步执行
# services 后台异步执行
# ============================================================

# 切换到本脚本所在目录, 使后续相对路径 (各 *-build 子目录) 可靠
cd "$(dirname "$0")"

# 1. 构建父工程: 前台同步等待其完成
cd maozi-cloud-parent-build
./maozi-cloud-parent-build.sh

# 2. 构建基础服务: 后台执行
cd ../maozi-cloud-basics-build
./maozi-cloud-basics-build.sh &

# 3. 构建业务服务: 后台执行
cd ../maozi-cloud-services-build
./maozi-cloud-services-build.sh &

# 等待后台的 basics / services 构建结束
wait
