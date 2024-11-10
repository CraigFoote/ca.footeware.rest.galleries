FROM eclipse-temurin:21-jre-noble
ARG JAR_FILE
ARG JAR_NAME
ADD ${JAR_FILE} /opt/rest.galleries/${JAR_NAME}
EXPOSE 8000
ENV ENV_JAR_NAME=$JAR_NAME
ENTRYPOINT java -jar /opt/rest.galleries/${ENV_JAR_NAME}