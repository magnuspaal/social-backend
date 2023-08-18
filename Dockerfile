FROM openjdk:17-alpine
ARG APP_VERSION

COPY ./target/social-${APP_VERSION}.jar /usr/local/lib/social.jar

EXPOSE 8080

ENTRYPOINT java -jar /usr/local/lib/social.jar