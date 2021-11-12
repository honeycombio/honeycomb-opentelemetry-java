FROM openjdk:17-jdk-alpine as base
ENV HONEYCOMB_API_ENDPOINT=http://collector:4317
ENV HONEYCOMB_API_KEY=bogus_key
ENV HONEYCOMB_DATASET=bogus_dataset

FROM base as sdk
COPY jars/spring-sdk.jar /app.jar
EXPOSE 5001
CMD ["java", "-jar", "/app.jar"]

FROM base as base-with-agent
COPY jars/agent.jar /agent.jar
CMD ["java", "-javaagent:/agent.jar", "-jar", "/app.jar"]

FROM base-with-agent as agent-manual
EXPOSE 5000
COPY jars/spring-agent-manual.jar /app.jar

FROM base-with-agent as agent-only
EXPOSE 5002
COPY jars/spring-agent-only.jar /app.jar

