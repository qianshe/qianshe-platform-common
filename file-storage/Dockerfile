# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装curl用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 复制jar包
COPY target/file-storage-*.jar app.jar

# 创建文件存储目录
RUN mkdir -p /app/files

# 创建健康检查脚本
RUN echo '#!/bin/bash' > /app/healthcheck.sh && \
    echo 'set -e' >> /app/healthcheck.sh && \
    echo 'if [ "$1" = "live" ]; then' >> /app/healthcheck.sh && \
    echo '  curl -f -s http://localhost:8092/actuator/health/liveness || exit 1' >> /app/healthcheck.sh && \
    echo 'elif [ "$1" = "ready" ]; then' >> /app/healthcheck.sh && \
    echo '  curl -f -s http://localhost:8092/actuator/health/readiness || exit 1' >> /app/healthcheck.sh && \
    echo 'else' >> /app/healthcheck.sh && \
    echo '  curl -f -s http://localhost:8092/actuator/health || exit 1' >> /app/healthcheck.sh && \
    echo 'fi' >> /app/healthcheck.sh && \
    chmod +x /app/healthcheck.sh

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# 暴露端口
EXPOSE 8092

# Docker健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD /app/healthcheck.sh

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
