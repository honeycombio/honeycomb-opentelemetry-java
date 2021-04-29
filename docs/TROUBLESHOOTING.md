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

#### Gradle, excluding `grpc-netty-shaded`

```groovy
dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0') {
        exclude group: 'io.grpc', module: 'grpc-netty-shaded'
    }
}
```

### SDK Configuration

Import the relevant packages:

```java
import io.honeycomb.opentelemetry.HoneycombSdk;
import io.honeycomb.openteletmry
```

Initialize the Honeycomb SDK using the builder:

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
// ... do some cool stuff
span.setAttribute("coolness", 100);
span.end();
```
