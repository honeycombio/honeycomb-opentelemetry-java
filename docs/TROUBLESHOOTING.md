# Troubleshooting the Honeycomb OpenTelemetry Distro for Java

## Seeing gRPC spans labeled `export`

If you are seeing gRPC spans with the Export function (honeycomb RPC),
it is likely because the auto-instrumentation agent is instrumenting
your manual SDK.
Don't use the manual SDK tracer.

## Missing custom spans

If you are not seeing custom spans, added from in-code instrumentation,
it is likely because you are using incompatible versions of the agent and SDK.

## SDK Usage

For teams that opt not to use the agent for auto-instrumentation,
the Honeycomb OpenTelemetry SDK provides convenient setup
for sending manual OpenTelemetry instrumentation to Honeycomb.
The SDK also provides a deterministic sampler and more span processing options.

### Project Setup

#### Maven

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.1.1</version>
        </dependency>
    </dependencies>
</project>
```

#### Gradle

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.1')
}
```

### gRPC transport customization

A gRPC transport is required to transmit OpenTelemetry data.
HoneycombSDK includes `grpc-netty-shaded`.
If you'd like to use another gRPC transport,
you can exclude the `grpc-netty-shaded` transitive dependency:

#### Maven, excluding `grpc-netty-shaded`

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.honeycomb</groupId>
            <artifactId>honeycomb-opentelemetry-sdk</artifactId>
            <version>0.1.1</version>
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
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.1') {
        exclude group: 'io.grpc', module: 'grpc-netty-shaded'
    }
}
```

### SDK Configuration

Import the relevant packages:

```java
import io.honeycomb.opentelemetry.HoneycombSdk;
import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler; // optional, for sampling
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor; // optional, for multi-span attributes
```

Initialize the Honeycomb SDK using the builder
in your application's entry point:

```java
HoneycombSdk honeycomb = new HoneycombSdk.Builder()
    .setApiKey(YOUR_API_KEY)
    .setDataset(YOUR_DATASET)
    .setSampler(new DeterministicTraceSampler(5)) // optional
    .addSpanProcessor(new BaggageSpanProcessor()) // optional, for multi-span attributes
    .setEndpoint(YOUR_ENDPOINT) // optional, defaults to api.honeycomb.io
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

#### Multi-span attributes

Sometimes you'll want to add the same attribute to many spans
within the same trace.
Using the OpenTelemetry concept of
[Baggage](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/overview.md#baggage-signal),
you can add attributes to child spans of the current span in your trace.

Be sure to initialize the `HoneycombSdk.Builder()`
with a `BaggageSpanProcessor`:

```java
import io.honeycomb.opentelemetry.HoneycombSdk;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;

HoneycombSdk honeycomb = new HoneycombSdk.Builder()
    .setApiKey(YOUR_API_KEY)
    .setDataset(YOUR_DATASET)
    .setServiceName(YOUR_SERVICE_NAME)
    ... // optional configuration options
    .addSpanProcessor(new BaggageSpanProcessor()) // for multi-span attributes
    .build();
```

In your code, import `io.opentelemetry.api.baggage.Baggage`
to allow use of the `Baggage` class.
From there, pass in the `key` and `value` you want added as an attribute
to every subsequent child span of the current application context:

```java
Baggage.current()
    .toBuilder()
    .put(key, value)
    .build()
    .makeCurrent();
```
