# Honeycomb OpenTelemetry Distro for Java

[![CircleCI](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java.svg?style=shield&circle-token=e2f4c30919ecbdbfb095415a6f4114a03dc491a0)](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java)

**STATUS: this library is BETA.** You're welcome to try it, and let us know your feedback in the issues!

This is Honeycomb's distribution of OpenTelemetry for Java. It makes getting started with OpenTelemetry and Honeycomb easier!

## Why would I want to use this?

- Streamlined configuration for sending data to Honeycomb!
- Easy interop with existing instrumentation with OpenTelemetry!
- Deterministic sampling!
- Multi-span attributes!

## Getting Started

There are two ways to start using Honeycomb OpenTelemetry:

1. If you are looking for an all-in-one, easy-to-install auto-instrumentation for your Java application, you'll want to use the `honeycomb-opentelemetry-javaagent`.
See the [Agent Usage](#agent-usage) section for setup.
You can enrich your application's auto-instrumented telemetry by adding [custom instrumentation](#enrich-the-auto-instrumented-data) to your application code.
2. If you want start first with manually instrumenting your application and are not interested in auto-instrumentation, you'll want to use the Honeycomb OpenTelemetry SDK. See the [SDK Usage](#sdk-usage) section for details.

## Agent Usage

Download the [latest version](https://github.com/honeycombio/honeycomb-opentelemetry-java/releases/download/v0.1.0/honeycomb-opentelemetry-javaagent-0.1.0-all.jar).

The agent is run as a `-javaagent` alongside your application.

```sh
java -javaagent:honeycomb-opentelemetry-javaagent-0.1.0-all.jar -jar myapp.jar
```

### Configuration

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
java -javaagent:honeycomb-opentelemetry-javaagent-0.1.0-all.jar -jar myapp.jar
```

Using system properties:

```sh
java \
-Dsample.rate=2 \
-Dservice.name=my-favorite-service \
-Dhoneycomb.api.key=my-api-key \
-Dhoneycomb.dataset=my-dataset \
-javaagent:honeycomb-opentelemetry-javaagent-0.1.0-all.jar -jar myapp.jar
```

### Enrich the Auto-Instrumented Data

When using the Honeycomb OpenTelemetry Agent, you can add custom instrumentation directly to auto-instrumented trace and span contexts using the vanilla OpenTelemetry SDK.

Add the OpenTelemetry packages to your project's dependencies.

For Maven:
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-api</artifactId>
            <version>1.0.1</version>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-extension-annotations</artifactId>
            <version>1.0.1</version>
        </dependency>
    </dependencies>
</project>
```

For Gradle:

```groovy
dependencies {
    compile('io.opentelemetry:opentelemetry-api:1.0.1')
    compile('io.opentelemetry:opentelemetry-extension-annotations:1.0.1')
}
```

Then, import the relevant OpenTelemetry SDK package into your code.
Here's an example adding a custom attribute to a span created by the agent for a Spring controller:

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

### Multi-span Attributes

Sometimes you'll want to add the same attribute to many spans within the same trace.
We'll leverage the OpenTelemetry concept of [Baggage](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/overview.md#baggage-signal) to do that.

Use this to add a `key` with a `value` as an attribute to every subsequent child span of the current application context.

```java
Baggage.current()
    .toBuilder()
    .put(key, value)
    .build()
    .makeCurrent();
```

## SDK Usage

For teams that opt not to use the agent for auto-instrumentation, the Honeycomb OpenTelemetry SDK provides convenient setup for sending manual OpenTelemetry instrumentation to Honeycomb. The SDK also provides a deterministic sampler and more span processing options.

### Project Setup

#### Maven

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

#### Gradle

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0')
}
```

### gRPC transport customization

A gRPC transport is required to transmit OpenTelemetry data. HoneycombSDK includes `grpc-netty-shaded`.
If you'd like to use another gRPC transport, you can exclude the `grpc-netty-shaded` transitive dependency:

#### Maven

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

#### Gradle

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0') {
        exclude group: 'io.grpc', module: 'grpc-netty-shaded'
    }
}
```

### SDK Configuration

```java
import io.honeycomb.opentelemetry.HoneycombSdk;
import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;

HoneycombSdk honeycomb = new HoneycombSdk.Builder()
    .setApiKey(YOUR_API_KEY)
    .setDataset(YOUR_DATASET)
    .setSampler(new DeterministicTraceSampler(5)) // optional
    .setEndpoint(YOUR_ENDPOINT) // optional
    .setServiceName(YOUR_SERVICE_NAME)
    .build();
```

The `HoneycombSdk` instance can then be used to create a Tracer:

```java
Tracer tracer = honeycomb.getTracer("instrumentation-name");
Span span = tracer.spanBuilder("my-span").startSpan();
// ... do some cool stuff
span.setAttribute("coolness", 100);
span.end();
```

## License

[Apache 2.0 License](./LICENSE).
