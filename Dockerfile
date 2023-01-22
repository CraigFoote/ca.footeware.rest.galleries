FROM ubuntu:latest
RUN apt update && \
    apt install -y openjdk-19-jdk ca-certificates-java && \
    apt clean && \
    update-ca-certificates -f
ENV JAVA_HOME /usr/lib/jvm/java-19-openjdk-amd64/
RUN export JAVA_HOME
VOLUME /opt/galleries
RUN mkdir -p /opt/rest.galleries/logs/
ARG JAR_FILE
ADD ${JAR_FILE} /opt/rest.galleries/app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/opt/rest.galleries/app.jar"]