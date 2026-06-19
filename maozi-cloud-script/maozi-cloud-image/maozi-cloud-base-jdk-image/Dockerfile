# AMD架构
#FROM eclipse-temurin:17-jre-alpine

# ARM架构
FROM eclipse-temurin:17-jre-jammy

# 时区设置
RUN mkdir -p /${APP_NAME} && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone

# 安装 dumb-init（Ubuntu/Debian 系统用 apt，Alpine 用 apk）
RUN apt update && apt install -y dumb-init && rm -rf /var/lib/apt/lists/*