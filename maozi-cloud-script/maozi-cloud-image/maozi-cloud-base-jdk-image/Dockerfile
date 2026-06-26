# ============================================================
# 平台公共基础镜像 —— 所有业务/网关镜像的 FROM 基础
# ------------------------------------------------------------
# Tag 命名：maozi-cloud-base-jdk:1.0.0
# 由所有 *-service-image（oauth/system）与 *-basics-image（gateway/monitor）继承。
# ------------------------------------------------------------
# 架构选择：
#   - 当前启用 ARM 架构（eclipse-temurin:17-jre-jammy）；
#   - AMD 架构（eclipse-temurin:17-jre-alpine）已注释，切换时取消注释、注释上方即可。
# ------------------------------------------------------------
# 注意：
#   1. 时区固定 Asia/Shanghai（与 promtail 解析、日志输出对齐）；
#   2. dumb-init 作为 PID 1 的「init」进程，负责回收僵尸进程与转发信号；
#      下方 ${APP_NAME} 在本基础镜像构建时未显式赋值，
#      仅创建 / 目录占位，业务子镜像会再次 mkdir 实际目录。
# ============================================================

# AMD架构
#FROM eclipse-temurin:17-jre-alpine

# ARM架构
FROM eclipse-temurin:17-jre-jammy

# 时区设置：固定为 Asia/Shanghai
RUN mkdir -p /${APP_NAME} && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone

# 安装 dumb-init（Ubuntu/Debian 系统用 apt，Alpine 用 apk）
RUN apt update && apt install -y dumb-init && rm -rf /var/lib/apt/lists/*