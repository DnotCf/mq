FROM docker.clzytech.com:1024/library/nginx

ADD dist /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

## 解决时区的问题
ENV TZ=Asia/Shanghai

## 解决中文乱码
ENV LC_ALL en_US.UTF-8

EXPOSE 80
