# ============================================================
# 平台最底层基础镜像 —— 仅含 OS + 时区 + dumb-init
# ------------------------------------------------------------
# Tag 命名：maozi-cloud-base-jdk:1.0.0
# ------------------------------------------------------------
# 上游被两类镜像 FROM:
#   1. maozi-cloud-business-jdk:1.0.0
#        本镜像 + OpenTelemetry Agent, 供启用 OTel 的服务 FROM
#        (system / oauth / gateway 等). 见 maozi-cloud-business-jdk-image/Dockerfile
#   2. 不启用 OTel 的服务直接 FROM 本镜像 (如 maozi-cloud-monitor-service),
#      镜像体积更小, 不携带 OTel Agent jar.
# ------------------------------------------------------------
# 架构选择:
#   - 当前启用 ARM 架构 (eclipse-temurin:17-jre-jammy);
#   - AMD 架构 (eclipse-temurin:17-jre-alpine) 已注释, 切换时取消注释、注释上方即可.
# ------------------------------------------------------------
# 注意:
#   1. 时区固定 Asia/Shanghai (与 promtail 解析、日志输出对齐);
#   2. dumb-init 作为 PID 1 的「init」进程, 负责回收僵尸进程与转发信号;
#   3. 下方 ${APP_NAME} 在本基础镜像构建时未显式赋值,
#      仅创建 / 目录占位, 业务子镜像会再次 mkdir 实际目录.
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
