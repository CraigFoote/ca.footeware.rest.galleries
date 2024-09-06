FROM amd64/eclipse-temurin:22-jre
#RUN mkdir -p /opt/rest.galleries/logs
ARG JAR_FILE
ADD ${JAR_FILE} /opt/rest.galleries/app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/opt/rest.galleries/app.jar"]