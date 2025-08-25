# 使用多阶段构建
FROM openjdk:17-jdk-slim as builder
WORKDIR /build
# 复制 jar 包
COPY target/gateway-*.jar app.jar
# 提取 jar 包内容
RUN java -Djarmode=layertools -jar app.jar extract

# 最终镜像
FROM openjdk:17-jdk-slim
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加标签
LABEL maintainer="qianshe"
LABEL version="1.0"
LABEL description="Fantasy Center Gateway Service"

# 复制分层的 jar
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./

# 暴露端口
EXPOSE 8080 9000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 设置 JVM 参数
ENV JAVA_OPTS="\
    -server \
    -Xms256m \
    -Xmx512m \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heap-dump.hprof \
    -XX:+UseContainerSupport \
    "

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]

