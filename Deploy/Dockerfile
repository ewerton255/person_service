FROM adoptopenjdk/openjdk11:jdk-11.0.9.1_1-alpine-slim

RUN apk --no-cache add curl && \
    mkdir -p /logs && \
    mkdir -p /config && \
    mkdir -p /security && \
    addgroup -g 9999 app && \
    adduser \
    --disabled-password \
    --gecos "" \
    --ingroup "app" \
    --no-create-home \
    --uid "9999" \
    "app"

COPY ./app.jar /app.jar

RUN chown -R app:app ./app.jar
USER app

ENTRYPOINT java -jar app.jar