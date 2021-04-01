package io.honeycomb.opentelemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;

public final class HoneycombSdkBuilder {

    private final String HoneycombTeamHeader = "X-Honeycomb-Team";
    private final String HoneycombDatasetHeader = "X-Honeycomb-Dataset";
    private final String defaultEndpoint = "https://api.honeycomb.io";

    private ContextPropagators propagators;
    private String apiKey;
    private String dataset;
    private String endpoint;

    HoneycombSdkBuilder() {}

    public HoneycombSdkBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public HoneycombSdkBuilder setDataset(String dataset) {
        this.dataset = dataset;
        return this;
    }

    public HoneycombSdkBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public HoneycombSdkBuilder setContextPropagators(ContextPropagators propagators) {
        this.propagators = propagators;
        return this;
    }

    public HoneycombSdk buildAndRegisterGlobal() {
        HoneycombSdk sdk = build();
        GlobalOpenTelemetry.set(sdk);
        return sdk;
    }

    public HoneycombSdk build() {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();

        if (endpoint == null) {
            builder.setEndpoint(endpoint);
        } else {
            builder.setEndpoint(defaultEndpoint);
        }

        SpanExporter exporter = builder
                .addHeader(HoneycombTeamHeader, apiKey)
                .addHeader(HoneycombDatasetHeader, dataset)
                .build();
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build()).build();

        if (propagators == null) {
            propagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        }

        return new HoneycombSdk(tracerProvider, propagators);
    }
}
