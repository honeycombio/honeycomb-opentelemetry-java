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
java -javaagent:honeycomb-opentelemetry-javaagent-0.1.0-all.jar -jar java-example-webapp-1.0.0.jar
```

Using system properties:

```sh
java \
-Dsample.rate=2 \
-Dservice.name=my-favorite-service \
-Dhoneycomb.api.key=my-api-key \
-Dhoneycomb.dataset=my-dataset \
-javaagent:honeycomb-opentelemetry-javaagent-0.1.0-all.jar -jar java-example-webapp-1.0.0.jar
```

### Custom instrumentation with agent

If you're using the Honeycomb OpenTelemetry Agent, you can add custom instrumentation directly to auto-instrumented trace and span contexts using the vanilla OpenTelemetry SDK.

Add the OpenTelemetry Packages to your project's dependencies.
For Gradle:

```groovy
dependencies {
    compile('io.opentelemetry:opentelemetry-api:1.0.0')
    compile('io.opentelemetry:opentelemetry-extension-annotations:1.0.0')
}
```

Then, import the relevant OpenTelemetry SDK package into your code.
Here's an example adding custom instrumentation to an auto-instrumented Spring controller:

```java
// import OpenTelemetry package into your code
import io.opentelemetry.api.trace.Span;

@RestController
public class ExampleController {
    @RequestMapping("/")
    public String index() {
        // access the current context and add a custom attribute
        Span span = Span.current();
        span.setAttribute("custom_field", "important value");
        return "hello world";
    }
}
```

## SDK Usage

Teams using the Honeycomb OpenTelemetry Agent won't need to set up the Honeycomb OpenTelemetry SDK.
For teams that opt not to use the agent, Honeycomb OpenTelemetry SDK provides a convenient builder syntax for configuration.

### Maven

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.1.0</version>
        </dependency>
    </dependencies>
</project>
```

### Gradle

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0')
}
```

### gRPC transport

A gRPC transport is required to transmit OpenTelemetry data. HoneycombSDK includes `grpc-netty-shaded`.
If you'd like to use another gRPC transport, you can exclude the `grpc-netty-shaded` transitive dependency:

Maven
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.1.0</version>
            <exclusions>
                <exclusion>
                    <groupId>io.grpc</groupId>
                    <artifactId>grpc-netty-shaded</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
</project>
```

Gradle
```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0') {
        exclude group: 'io.grpc', module: 'grpc-netty-shaded'
    }
}
```

### Setup

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
