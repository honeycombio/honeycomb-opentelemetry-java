# Honeycomb OpenTelemetry Distro for Java

[![CircleCI](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java.svg?style=shield&circle-token=e2f4c30919ecbdbfb095415a6f4114a03dc491a0)](https://circleci.com/gh/honeycombio/honeycomb-opentelemetry-java)

This is a library for using OpenTelemetry Java with Honeycomb. It makes it easy to get started.

## Usage

The Honeycomb OpenTelemetry Distro provides a convenient builder syntax for configuring the OpenTelemetry SDK:

    HoneycombSdk honeycomb = new HoneycombSdk.Builder()
        .setApiKey(YOUR_API_KEY)
        .setDataset(YOUR_DATASET)
        .setSampler(new DeterministicSampler(5)) // optional
        .setEndpoint(YOUR_ENDPOINT) // optional
        .setServiceName(YOUR_SERVICE_NAME)
        .build();

The HoneycombSdk instance can then be used to create a Tracer:

    Tracer tracer = honeycomb.getTracer("instrumentation-name");
    Span span = tracer.spanBuilder("my-span").startSpan();

## License

[Apache 2.0 License](./LICENSE).
