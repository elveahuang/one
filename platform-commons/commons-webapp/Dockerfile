# 镜像
FROM bellsoft/liberica-openjdk-rocky:25
LABEL maintainer="Elvea Huang"
# 处理构建文件
RUN mkdir -p /app
COPY ./build/libs/app.jar /app/app.jar
# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone
# 设置编码
ENV LANG C.UTF-8
# 启动应用
WORKDIR /app
ENTRYPOINT ["java", "-Xms512M", "-Xmx512M", "-Dfile.encoding=UTF-8", "-XX:+HeapDumpOnOutOfMemoryError", "-jar", "/app/app.jar" ]
