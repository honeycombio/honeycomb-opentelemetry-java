# Honeycomb OpenTelemetry Distro for Java

[![OSS Lifecycle](https://img.shields.io/osslifecycle/honeycombio/honeycomb-opentelemetry-java)](https://github.com/honeycombio/home/blob/main/honeycomb-oss-lifecycle-and-practices.md)
[![CircleCI](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java.svg?style=shield&circle-token=e2f4c30919ecbdbfb095415a6f4114a03dc491a0)](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java)

**STATUS: this library is BETA.**
You're welcome to try it, and let us know your feedback in the issues!

This is Honeycomb's distribution of OpenTelemetry for Java.
It makes getting started with OpenTelemetry and Honeycomb easier!

Latest release built with:
- [OpenTelemetry](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v1.6.0) version 1.6.0
- [OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.6.0) version 1.6.0

## Why would I want to use this?

- Streamlined configuration for sending data to Honeycomb!
- Easy interop with existing instrumentation with OpenTelemetry!
- Deterministic sampling!
- Multi-span attributes!

## Getting Started

Honeycomb's OpenTelemetry Java Agent gives you all-in-one,
easy-to-install auto-instrumentation for your Java application.
See the [Agent Usage](#agent-usage) section for setup.

Otherwise, if you're not interested in auto-instrumentation, and you'd like to start first with manually instrumenting your application,
you can use the Honeycomb OpenTelemetry SDK.
See the [SDK Usage](https://docs.honeycomb.io/getting-data-in/java/opentelemetry-distro/#using-the-honeycomb-sdk-builder) instructions for details.

## Agent Usage

Download the [latest version](https://github.com/honeycombio/honeycomb-opentelemetry-java/releases/download/v0.5.0/honeycomb-opentelemetry-javaagent-0.5.0-all.jar).

The agent is run as a `-javaagent` alongside your application.

```sh
java -javaagent:honeycomb-opentelemetry-javaagent-0.5.0-all.jar -jar myapp.jar
```

### Configuration

The Honeycomb Agent has the following configuration options (system properties take precedence over environment variables):

| System property                      | Environment variable                 | Description                                                                      |
|--------------------------------------|--------------------------------------|----------------------------------------------------------------------------------|
| `honeycomb.api.key` | `HONEYCOMB_API_KEY` | [optional] Your Honeycomb API key
| `honeycomb.traces.apikey` | `HONEYCOMB_TRACES_APIKEY` | [optional] Your Honeycomb traces API key (defaults to the value of `HONEYCOMB_API_KEY`)
| `honeycomb.metrics.apikey` | `HONEYCOMB_METRICS_APIKEY` | [optional] Your Honeycomb metrics API key (defaults to the value of `HONEYCOMB_API_KEY`)
| `honeycomb.dataset` | `HONEYCOMB_DATASET` | [optional] Honeycomb dataset where data will be sent
| `honeycomb.traces.dataset` | `HONEYCOMB_TRACES_DATASET` | [optional] Honeycomb dataset where traces will be sent (defaults to the value of `HONEYCOMB_DATASET`)
| `honeycomb.metrics.dataset` | `HONEYCOMB_METRICS_DATASET` | [optional] Honeycomb dataset where metrics will be sent (defaults to null - metrics will not be exported if dataset is not defined)
| `honeycomb.api.endpoint` | `HONEYCOMB_API_ENDPOINT` | [optional] Honeycomb ingest endpoint (defaults to https://api.honeycomb.io:443)
| `honeycomb.traces.endpoint` | `HONEYCOMB_TRACES_ENDPOINT` | [optional] Honeycomb traces ingest endpoint (defaults to the value of `HONEYCOMB_API_ENDPOINT`)
| `honeycomb.metrics.endpoint` | `HONEYCOMB_METRICS_ENDPOINT` | [optional] Honeycomb metrics ingest endpoint (defaults to the value of `HONEYCOMB_API_ENDPOINT`)
| `sample.rate` | `SAMPLE_RATE` | [optional] Sample rate for the deterministic sampler (defaults to 1, always sample)
| `service.name` | `SERVICE_NAME` | [optional] `service.name` attribute to be used for all spans (defaults to empty)

Using environment variables:

```sh
SAMPLE_RATE=2 \
SERVICE_NAME=my-favorite-service \
HONEYCOMB_API_KEY=my-api-key \
HONEYCOMB_DATASET=my-dataset \
java -javaagent:honeycomb-opentelemetry-javaagent-0.5.0-all.jar -jar myapp.jar
```

Using system properties:

```sh
java \
-Dsample.rate=2 \
-Dservice.name=my-favorite-service \
-Dhoneycomb.api.key=my-api-key \
-Dhoneycomb.dataset=my-dataset \
-javaagent:honeycomb-opentelemetry-javaagent-0.5.0-all.jar -jar myapp.jar
```

### Enrich the Auto-Instrumented Data

When using the Honeycomb OpenTelemetry Agent,
you can add custom instrumentation directly to auto-instrumented trace
and span contexts using the OpenTelemetry API.

Honeycomb SDK provides the OpenTelemetry API as a transitive dependency:

For Maven:

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.5.0</version>
        </dependency>
    </dependencies>
</project>
```

For Gradle:

```groovy
dependencies {
    compile('io.honeycomb:honeycomb-opentelemetry-sdk:0.5.0')
}
```

Then, import the relevant OpenTelemetry API into your code.
Here's an example adding a custom attribute to a span created by the agent
for a Spring controller:

```java
// import OpenTelemetry API into your code
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

### Resource Attributes

Sometimes you'll want one or more attributes set on all spans within a service.

Using environment variables:

```sh
OTEL_RESOURCE_ATTRIBUTES=ec2.instanceid=i-1234567890abcdef0,build_id=1337 \
SERVICE_NAME=my-favorite-service \
HONEYCOMB_API_KEY=my-api-key \
HONEYCOMB_DATASET=my-dataset \
java -javaagent:honeycomb-opentelemetry-javaagent-0.5.0-all.jar -jar myapp.jar
```

Using system properties:

```sh
java \
-Dotel.resource.attributes=ec2.instanceid=i-1234567890abcdef0,build_id=1337 \
-Dservice.name=my-favorite-service \
-Dhoneycomb.api.key=my-api-key \
-Dhoneycomb.dataset=my-dataset \
-javaagent:honeycomb-opentelemetry-javaagent-0.5.0-all.jar -jar myapp.jar
```

### Multi-span Attributes

Sometimes you'll want to add the same attribute to many spans
within the same trace.
We'll leverage the OpenTelemetry concept of
[Baggage](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/overview.md#baggage-signal)
to do that.

Use this to add a `key` with a `value` as an attribute
to every subsequent child span of the current application context.

In your code, import `io.opentelemetry.api.baggage.Baggage`
to allow use of the `Baggage` class.

```java
Baggage.current()
    .toBuilder()
    .put(key, value)
    .build()
    .makeCurrent();
```

## SDK Usage

For teams that opt not to use the agent for auto-instrumentation,
the Honeycomb OpenTelemetry SDK provides convenient setup
for sending manual OpenTelemetry instrumentation to Honeycomb.
The SDK also provides a deterministic sampler and more span processing options.

[Set up the Honeycomb OpenTelemetry SDK for Java](https://docs.honeycomb.io/getting-data-in/java/opentelemetry-distro/#using-the-honeycomb-sdk-builder).

```java
import io.honeycomb.opentelemetry.OpenTelemetryConfiguration;

@Bean
public OpenTelemetry honeycomb() {
    return OpenTelemetryConfiguration.builder()
        .setApiKey(System.getenv("HONEYCOMB_API_KEY"))
        .setDataset(System.getenv("HONEYCOMB_DATASET"))
        .setServiceName(System.getenv("SERVICE_NAME"))
        .setEndpoint(System.getenv("HONEYCOMB_API_ENDPOINT"))
        .buildAndRegisterGlobal();
    }
```

## Troubleshooting

### Debug Mode

To enable debugging when running with the OpenTelemetry Java Agent, you can set the `otel.javaagent.debug` system property or `OTEL_JAVAAGENT_DEBUG` environment variable to `true`.
When this setting is provided, the Agent configures a [LoggingSpanExporter](https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/logging) that logs traces & metrics data.

### gRPC transport customization

A gRPC transport is required to transmit OpenTelemetry data.
HoneycombSDK includes `grpc-netty-shaded`.

If you're using another gRPC dependency, version conflicts can come up with an error like this:

```cmd
io/grpc/ClientStreamTracer$StreamInfo$Builder.setPreviousAttempts(I)Lio/grpc/ClientStreamTracer$StreamInfo$Builder; (loaded from file:/app.jar by jdk.internal.loader.ClassLoaders$AppClassLoader@193b9e51) called from class io.grpc.internal.GrpcUtil (loaded from file:/io.grpc/grpc-core/1.41.0/882b6572f7d805b9b32e3993b1d7d3e022791b3a/grpc-core-1.41.0.jar by jdk.internal.loader.ClassLoaders$AppClassLoader@193b9e51).
java.lang.NoSuchMethodError: io/grpc/ClientStreamTracer$StreamInfo$Builder.setPreviousAttempts(I)Lio/grpc/ClientStreamTracer$StreamInfo$Builder; (loaded from file:/app.jar by jdk.internal.loader.ClassLoaders$AppClassLoader@193b9e51) called from class io.grpc.internal.GrpcUtil (loaded from file:/io.grpc/grpc-core/1.41.0/882b6572f7d805b9b32e3993b1d7d3e022791b3a/grpc-core-1.41.0.jar by jdk.internal.loader.ClassLoaders$AppClassLoader@193b9e51).
```

If you'd like to use another gRPC transport,
you can exclude the `grpc-netty-shaded` transitive dependency:

#### Maven, excluding `grpc-netty-shaded`

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.5.0</version>
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

#### Gradle, excluding `grpc-netty-shaded`

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.5.0') {
        exclude group: 'io.grpc', module: 'grpc-netty-shaded'
    }
}
```

## License

[Apache 2.0 License](./LICENSE).
