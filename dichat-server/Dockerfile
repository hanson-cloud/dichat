# 服务特定的 Dockerfile
FROM eclipse-temurin:21-jre-alpine

## 设置工作目录
WORKDIR /app

## 将后端项目的 Jar 文件，复制到镜像中
COPY ./target/*.jar app.jar

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

## 创建启动脚本
RUN echo $'#!/bin/sh\n\
exec java ${JAVA_OPTS} -jar app.jar' > /app/run.sh \
    && chmod +x /app/run.sh

## 使用 exec 形式的 CMD
CMD ["/app/run.sh"]
