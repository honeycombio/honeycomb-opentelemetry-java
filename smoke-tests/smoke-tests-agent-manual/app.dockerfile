FROM openjdk:17-jdk-alpine

ENV HONEYCOMB_API_ENDPOINT=http://collector:4317
ENV HONEYCOMB_API_KEY=bogus_key
ENV HONEYCOMB_DATASET=bogus_dataset

COPY agent.jar /agent.jar
COPY app.jar /app.jar
CMD ["java", "-javaagent:/agent.jar", "-jar", "/app.jar"]
EXPOSE 5000
