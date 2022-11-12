FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER w3rpa@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

#RUN mkdir -p /jeecg-boot/config/jeecg/

WORKDIR /rpa-server

EXPOSE 8080

#ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/rpa-server-start-1.0.0.jar ./

CMD sleep 60;java -Djava.security.egd=file:/dev/./urandom -jar rpa-server-start-1.0.0.jar