## AdoptOpenJDK 停止发布 OpenJDK 二进制，而 Eclipse Temurin 是它的延伸，提供更好的稳定性
## 感谢复旦核博士的建议！灰子哥，牛皮！
FROM eclipse-temurin:21-jre

## 视频封面（首帧）抽帧依赖 ffmpeg / ffprobe。
## eclipse-temurin 基于 Debian，使用 apt 安装；若生产环境 ffmpeg 不在镜像中，
## 也可在 Nacos 的 dichat.im.file.ffmpeg-path / ffprobe-path 配置绝对路径。
## 注：抽帧失败仅导致视频无封面（前端回退播放键），不影响视频上传。
USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

## 创建目录，并使用它作为工作目录
RUN mkdir -p /dichat-module-im-server
WORKDIR /dichat-module-im-server
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./target/dichat-module-im-server.jar app.jar

## 设置 TZ 时区
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms512m -Xmx512m"

## 暴露后端项目的 48093 端口
EXPOSE 48093

## 启动后端项目
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar
