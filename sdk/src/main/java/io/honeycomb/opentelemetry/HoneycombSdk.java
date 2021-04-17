package io.honeycomb.opentelemetry;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The Honeycomb SDK implementation of {@link OpenTelemetry}.
 *
 * This class exists to make it easier and more intuitive to use
 * Honeycomb with OpenTelemetry.
 */
public final class HoneycombSdk implements OpenTelemetry {

    private final SdkTracerProvider tracerProvider;
    private final ContextPropagators propagators;

    private HoneycombSdk(SdkTracerProvider tracerProvider, ContextPropagators propagators) {
        this.tracerProvider = tracerProvider;
        this.propagators = propagators;
    }

    @Override
    public TracerProvider getTracerProvider() {
        return this.tracerProvider;
    }

    @Override
    public ContextPropagators getPropagators() {
        return this.propagators;
    }

    public static class Builder {
        public Builder() {}

        private final String HONEYCOMB_TEAM_HEADER = "X-Honeycomb-Team";
        private final String HONEYCOMB_DATASET_HEADER = "X-Honeycomb-Dataset";
        private final String DEFAULT_ENDPOINT = "https://api.honeycomb.io";
        private final String SERVICE_NAME_FIELD = "service.name";

        private ContextPropagators propagators;
        private Sampler sampler = Sampler.alwaysOn();

        private String apiKey;
        private String dataset;
        private String endpoint;
        private String serviceName;

        /**
         * Sets the Honeycomb API Key to use.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the team making a request.</p>
         *
         * @param apiKey a String to use as the API key. See team settings in Honeycomb.
         */
        public Builder setApiKey(String apiKey) {
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
         */
        public Builder setDataset(String dataset) {
            this.dataset = dataset;
            return this;
        }

        /**
         * Sets the Honeycomb endpoint to use. Optional, defaults to the Honeycomb ingest API.
         *
         * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
         */
        public Builder setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Sets the service name as a resource attribute.
         *
         * @param serviceName a String to use as the service name
         */
        public Builder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        /**
         * Sets the {@link ContextPropagators} to use.
         *
         * <p>Note that if none are specified, {@link W3CTraceContextPropagator} will be used
         * by default.</p>
         */
        public Builder setPropagators(ContextPropagators propagators) {
            this.propagators = propagators;
            return this;
        }

        /**
         * Sets the {@link Sampler} to use.
         *
         * <p>Note that if no sampler is specified, AlwaysOnSampler</p>
         * will be used by default.</p>
         *
         * @param sampler Sampler instance
         */
        public Builder setSampler(Sampler sampler) {
            this.sampler = sampler;
            return this;
        }

        /**
         * Returns a new {@link HoneycombSdk} built with the configuration of this {@link
         * Builder} and registers it as the global {@link
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
         * Helper method for getting metadata from a local properties file.
         */
        private Attributes getMetadata() {
            AttributesBuilder builder = Attributes.builder();
            final Properties properties = new Properties();
            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream("sdk.properties"));
            } catch (IOException ignored) {}
            properties.forEach((k, v) -> {
                builder.put(k.toString(), v.toString());
            });
            if (serviceName != null) {
                builder.put(SERVICE_NAME_FIELD, serviceName);
            }
            return builder.build();
        }

        /**
         * Returns a new {@link HoneycombSdk} built with the configuration of this {@link
         * Builder}. This SDK is not registered as the global {@link
         * io.opentelemetry.api.OpenTelemetry}. It is recommended that you register one SDK using {@link
         * Builder#buildAndRegisterGlobal()} for use by instrumentation that requires
         * access to a global instance of {@link io.opentelemetry.api.OpenTelemetry}.
         *
         * <p>Note that the {@link Builder} automatically assigns a
         * {@link SdkTracerProvider} with a {@link BatchSpanProcessor} that has an
         * {@link OtlpGrpcSpanExporter} configured.</p>
         *
         * @see GlobalOpenTelemetry
         */
        public HoneycombSdk build() {
            Preconditions.checkNotNull(apiKey, "apiKey must be non-null");
            Preconditions.checkNotNull(dataset, "dataset must be non-null");
            Preconditions.checkNotNull(sampler, "sampler must be non-null");

            OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();

            if (endpoint != null) {
                builder.setEndpoint(endpoint);
            } else {
                builder.setEndpoint(DEFAULT_ENDPOINT);
            }

            SpanExporter exporter = builder
                .addHeader(HONEYCOMB_TEAM_HEADER, apiKey)
                .addHeader(HONEYCOMB_DATASET_HEADER, dataset)
                .build();

            SdkTracerProviderBuilder tracerProviderBuilder = SdkTracerProvider.builder()
                .setSampler(sampler)
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build());

            if (serviceName != null) {
                tracerProviderBuilder.setResource(Resource.create(getMetadata()));
            }

            if (propagators == null) {
                propagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
            }

            return new HoneycombSdk(tracerProviderBuilder.build(), propagators);
        }
    }

    /**
     * This class allows the SDK to unobfuscate an obfuscated static global provider.
     *
     * <p>Static global providers are obfuscated when they are returned from the API to prevent users
     * from casting them to their SDK specific implementation. For example, we do not want users to
     * use patterns like {@code (TracerSdkProvider) OpenTelemetry.getGlobalTracerProvider()}.
     */
    @ThreadSafe
    @VisibleForTesting
    static class ObfuscatedTracerProvider implements TracerProvider {

        private final SdkTracerProvider delegate;

        ObfuscatedTracerProvider(SdkTracerProvider delegate) {
            this.delegate = delegate;
        }

        @Override
        public Tracer get(String instrumentationName) {
            return delegate.get(instrumentationName);
        }

        @Override
        public Tracer get(String instrumentationName, String instrumentationVersion) {
            return delegate.get(instrumentationName, instrumentationVersion);
        }

        public SdkTracerProvider unobfuscate() {
            return delegate;
        }
    }
}
