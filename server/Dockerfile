FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/huarenzaimeng-server.jar app.jar
COPY start.sh start.sh

RUN chmod +x start.sh

EXPOSE 8080

ENTRYPOINT ["sh", "start.sh"]
