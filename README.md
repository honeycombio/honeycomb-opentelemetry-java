# Honeycomb OpenTelemetry Distro for Java

[![CircleCI](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java.svg?style=shield&circle-token=e2f4c30919ecbdbfb095415a6f4114a03dc491a0)](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java)

This is a library for using OpenTelemetry Java with Honeycomb. It makes it easy to get started.

## Agent Usage

The Honeycomb Agent has the following configuration options (system properties take precedence over environment variables):

| System property                      | Environment variable                 | Description                                                                      |
|--------------------------------------|--------------------------------------|----------------------------------------------------------------------------------|
| `honeycomb.api.key` | `HONEYCOMB_API_KEY` | [required] Your Honeycomb API key
| `honeycomb.dataset` | `HONEYCOMB_DATASET` | [required] Honeycomb dataset where events will be sent
| `honeycomb.api.endpoint` | `HONEYCOMB_API_ENDPOINT` | [optional] Honeycomb ingest endpoint (defaults to https://api.honeycomb.io:443)
| `sample.rate` | `SAMPLE_RATE` | [optional] Sample rate for the deterministic sampler (defaults to 1, no sampling)
| `service.name` | `SERVICE_NAME` | [optional] service.name attribute to be used for all events (defaults to empty)

Using environment variables:

```sh
SAMPLE_RATE=2 \
SERVICE_NAME=my-favorite-service \
HONEYCOMB_API_KEY=my-api-key \
HONEYCOMB_DATASET=my-dataset \
java -javaagent:agent-1.0-SNAPSHOT-all.jar -jar java-example-webapp-1.0.0.jar
```

Using system properties:

```sh
java \
-Dsample.rate=2 \
-Dservice.name=my-favorite-service \
-Dhoneycomb.api.key=my-api-key \
-Dhoneycomb.dataset=my-dataset \
-javaagent:agent-1.0-SNAPSHOT-all.jar -jar java-example-webapp-1.0.0.jar
```

## SDK Usage

The Honeycomb OpenTelemetry Distro provides a convenient builder syntax for configuring the OpenTelemetry SDK:

```java
HoneycombSdk honeycomb = new HoneycombSdk.Builder()
    .setApiKey(YOUR_API_KEY)
    .setDataset(YOUR_DATASET)
    .setSampler(new DeterministicSampler(5)) // optional
    .setEndpoint(YOUR_ENDPOINT) // optional
    .setServiceName(YOUR_SERVICE_NAME)
    .build();
```

The `HoneycombSdk` instance can then be used to create a Tracer:

```java
Tracer tracer = honeycomb.getTracer("instrumentation-name");
Span span = tracer.spanBuilder("my-span").startSpan();
```

## License

[Apache 2.0 License](./LICENSE).
