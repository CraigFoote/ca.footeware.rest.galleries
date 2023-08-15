FROM ubuntu:lunar
VOLUME /opt/galleries/
RUN apt update && \
    apt install -y openjdk-20-jre-headless ca-certificates-java && \
    apt autoclean && \
    update-ca-certificates -f
ENV JAVA_HOME /usr/lib/jvm/java-20-openjdk-amd64/
RUN export JAVA_HOME
RUN mkdir -p /opt/rest.galleries/logs/
ARG JAR_FILE
ADD ${JAR_FILE} /opt/rest.galleries/app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/opt/rest.galleries/app.jar"]