FROM eclipse-temurin:26-jre-resolute
ARG JAR_FILE
ARG JAR_NAME
ADD ${JAR_FILE} /opt/rest.galleries/${JAR_NAME}
EXPOSE 8000
ENV ENV_JAR_NAME=$JAR_NAME
ENTRYPOINT java $JAVA_OPTS -jar /opt/rest.galleries/${ENV_JAR_NAME}