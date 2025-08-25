# 使用多阶段构建
FROM openjdk:17-jdk-slim as builder
WORKDIR /build
# 复制 jar 包
COPY target/user-auth-*.jar app.jar
# 提取 jar 包内容
RUN java -Djarmode=layertools -jar app.jar extract

# 最终镜像
FROM openjdk:17-jdk-slim
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装字体依赖（解决X11FontManager问题）
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    # 可选：添加中文字体支持
    fonts-wqy-zenhei \
    && rm -rf /var/lib/apt/lists/*

# 添加标签
LABEL maintainer="qianshe"
LABEL version="1.0"
LABEL description="Fantasy Center User Auth Service"

# 复制分层的 jar
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# 修正后的 JVM 参数设置（注意每行结尾不要有多余的引号或空格）
ENV JAVA_OPTS="\
    -server \
    -Xms256m \
    -Xmx512m \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heap-dump.hprof \
    -XX:+UseContainerSupport \
    -Djava.awt.headless=true"
    # 最后一行不需要反斜杠

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]

