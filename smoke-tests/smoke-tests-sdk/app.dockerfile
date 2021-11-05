FROM openjdk:17-jdk-alpine

ENV HONEYCOMB_API_ENDPOINT=http://collector:4317
ENV HONEYCOMB_API_KEY=bogus_key
ENV HONEYCOMB_DATASET=bogus_dataset
ENV SERVICE_NAME="springbootsdk"

COPY app.jar /app.jar
EXPOSE 5001
CMD ["java", "-jar", "/app.jar"]
