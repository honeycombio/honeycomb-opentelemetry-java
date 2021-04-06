package io.honeycomb.opentelemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.Objects;

/**
 * A builder for configuring an {@link HoneycombSdk}
 */
public final class HoneycombSdkBuilder {

    private final String HoneycombTeamHeader = "X-Honeycomb-Team";
    private final String HoneycombDatasetHeader = "X-Honeycomb-Dataset";
    private final String defaultEndpoint = "https://api.honeycomb.io";

    private ContextPropagators propagators;
    private String apiKey;
    private String dataset;
    private String endpoint;

    /**
     * Prevent direct initialization. Should be instantiated by calling the
     * builder method in {@link HoneycombSdk}.
     * */
    HoneycombSdkBuilder() {}

    /**
     * Sets the Honeycomb API Key to use.
     *
     * <p>This value is sent to Honeycomb on every request and is used to identify
     * the team making a request.</p>
     *
     * @param apiKey a String to use as the API key. See team settings in Honeycomb.
     * @return HoneycombSdkBuilder instance.
     */
    public HoneycombSdkBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Sets the Honeycomb dataset to use.
     *
     * <p>This value is sent to Honeycomb on every request and is used to identify
     * the dataset that trace data is being written to.</p>
     *
     * @param dataset a String to use as the dataset name.
     *
     * @return HoneycombSdkBuilder instance.
     */
    public HoneycombSdkBuilder setDataset(String dataset) {
        this.dataset = dataset;
        return this;
    }

    /**
     * Sets the Honeycomb endpoint to use. Optional, defaults to the Honeycomb ingest API.
     *
     * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
     *
     * @return HoneycombSdkBuilder instance.
     */
    public HoneycombSdkBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * Sets the {@link ContextPropagators} to use.
     *
     * <p>Note that if none are specified, {@link W3CTraceContextPropagator} will be used
     * by default.</p>
     *
     * @return HoneycombSdkBuilder instance.
     */
    public HoneycombSdkBuilder setPropagators(ContextPropagators propagators) {
        this.propagators = propagators;
        return this;
    }

    /**
     * Returns a new {@link HoneycombSdk} built with the configuration of this {@link
     * HoneycombSdkBuilder} and registers it as the global {@link
     * io.opentelemetry.api.OpenTelemetry}. An exception will be thrown if this method is attempted to
     * be called multiple times in the lifecycle of an application - ensure you have only one SDK for
     * use as the global instance. If you need to configure multiple SDKs for tests, use {@link
     * GlobalOpenTelemetry#resetForTest()} between them.
     *
     * @see GlobalOpenTelemetry
     */
    public HoneycombSdk buildAndRegisterGlobal() {
        HoneycombSdk sdk = build();
        GlobalOpenTelemetry.set(sdk);
        return sdk;
    }

    /**
     * Returns a new {@link HoneycombSdk} built with the configuration of this {@link
     * HoneycombSdkBuilder}. This SDK is not registered as the global {@link
     * io.opentelemetry.api.OpenTelemetry}. It is recommended that you register one SDK using {@link
     * HoneycombSdkBuilder#buildAndRegisterGlobal()} for use by instrumentation that requires
     * access to a global instance of {@link io.opentelemetry.api.OpenTelemetry}.
     *
     * <p>Note that the {@link HoneycombSdkBuilder} automatically assigns a
     * {@link SdkTracerProvider} with a {@link BatchSpanProcessor} that has an
     * {@link OtlpGrpcSpanExporter} configured.</p>
     *
     * @see GlobalOpenTelemetry
     */
    public HoneycombSdk build() {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();

        if (endpoint != null) {
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
