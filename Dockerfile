FROM amd64/eclipse-temurin:19.0.1_10-jre-alpine
VOLUME /opt/galleries
RUN mkdir -p /opt/rest.galleries/logs/
ARG JAR_FILE
ADD ${JAR_FILE} /opt/rest.galleries/app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/opt/rest.galleries/app.jar"]