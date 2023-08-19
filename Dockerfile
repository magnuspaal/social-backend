FROM openjdk:17-alpine
ARG APP_VERSION

COPY ./target/social-backend-${APP_VERSION}.jar /usr/local/lib/social-backend.jar

EXPOSE 8080

ENTRYPOINT java -jar /usr/local/lib/social-backend.jar