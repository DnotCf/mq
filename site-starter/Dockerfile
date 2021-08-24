FROM docker.clzytech.com:1024/openjdk8-mrc2017b:0.1
WORKDIR /usr/local/dev
ADD target/mq-integration.jar site-starter.jar
ADD geo.lic config/

## 解决时区的问题
ENV TZ=Asia/Shanghai

## 解决中文乱码
ENV LC_ALL en_US.UTF-8

ENTRYPOINT ["/bin/sh","-c","java ${JAVA_OPTS} -jar site-starter.jar"]
