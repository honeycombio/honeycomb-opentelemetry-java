package io.honeycomb.opentelemetry;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static io.honeycomb.opentelemetry.EnvironmentConfiguration.isPresent;

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

        private ContextPropagators propagators;
        private Sampler sampler = Sampler.alwaysOn();
        private final List<SpanProcessor> additionalSpanProcessors = new ArrayList<>();
        private AttributesBuilder resourceAttributes = Attributes.builder();

        private String tracesApiKey;
        private String tracesDataset;
        private String tracesEndpoint;
        private String serviceName;
        private String metricsApiKey;
        private String metricsEndpoint;
        private String metricsDataset;

        /**
         * Sets the Honeycomb API Key to use.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the team making a request.</p>
         *
         * @param apiKey a String to use as the API key. See team settings in Honeycomb.
         * @return builder
         */
        public Builder setApiKey(String apiKey) {
            setTracesApiKey(apiKey);
            setMetricsApiKey(apiKey);
            return this;
        }

        /**
         * Sets the Honeycomb API key to send trace data.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the team making a request.</p>
         *
         * @param apiKey a String to use as the API key. See team settings in Honeycomb.
         * @return builder
         */
        public Builder setTracesApiKey(String apiKey) {
            this.tracesApiKey = apiKey;
            return this;
        }

        /**
         * Sets the Honeycomb API Key to send metrics data.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the team making a request.</p>
         *
         * @param apiKey a String to use as the API key. See team settings in Honeycomb.
         * @return builder
         */
        public Builder setMetricsApiKey(String apiKey) {
            this.metricsApiKey = apiKey;
            return this;
        }

        /**
         * Sets the Honeycomb dataset to use.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the dataset that trace data is being written to.</p>
         *
         * @param dataset a String to use as the dataset name.
         * @return builder
         */
        public Builder setDataset(String dataset) {
            setTracesDataset(dataset);
            // don't set metrics dataset, we want them to go to different places
            return this;
        }

        /**
         * Sets the Honeycomb dataset to store trace data.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the dataset that metrics data is being written to.</p>
         * @param dataset a String to use as the metrics dataset name.
         * @return builder
         */
        public Builder setTracesDataset(String dataset) {
            this.tracesDataset = dataset;
            return this;
        }

        /**
         * Sets the Honeycomb dataset to store metics data.
         *
         * <p>This value is sent to Honeycomb on every request and is used to identify
         * the dataset that metrics data is being written to.</p>
         * @param dataset a String to use as the metrics dataset name.
         * @return builder
         */
        public Builder setMetricsDataset(String dataset) {
            this.metricsDataset = dataset;
            return this;
        }

        /**
         * Sets the Honeycomb endpoint to use. Optional, defaults to the Honeycomb ingest API.
         *
         * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
         * @return builder
         */
        public Builder setEndpoint(String endpoint) {
            setTracesEndpoint(endpoint);
            setMetricsEndpoint(endpoint);
            return this;
        }

        /**
         * Sets the Honeycomb endpoint to send trace data. Optional, defaults to the Honeycomb ingest API.
         *
         * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
         * @return builder
         */
        public Builder setTracesEndpoint(String endpoint) {
            this.tracesEndpoint = endpoint;
            return this;
        }

        /**
         * Sets the Honeycomb endpoint to send metrics data.. Optional, defaults to the Honeycomb ingest API.
         *
         * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
         * @return builder
         */
        public Builder setMetricsEndpoint(String endpoint) {
            this.metricsEndpoint = endpoint;
            return this;
        }

        /**
         * Sets the service name as a resource attribute.
         *
         * @param serviceName a String to use as the service name
         * @return builder
         */
        public Builder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        /**
         * Sets the {@link ContextPropagators} to use.
         *
         * <p>Note that if none are specified, {@link W3CTraceContextPropagator} and {@link W3CBaggagePropagator}
         * will be used by default.</p>
         *
         * @param propagators {@link ContextPropagators} to use for context propagation
         * @return builder
         */
        public Builder setPropagators(ContextPropagators propagators) {
            this.propagators = propagators;
            return this;
        }

        /**
         * Sets the {@link Sampler} to use.
         *
         * <p>Note that if no sampler is specified, AlwaysOnSampler
         * will be used by default.</p>
         *
         * @param sampler Sampler instance
         * @return builder
         */
        public Builder setSampler(Sampler sampler) {
            this.sampler = sampler;
            return this;
        }

        /**
         * Configures additional {@link SpanProcessor}.
         *
         * {@link BatchSpanProcessor} is always configured by default. You can specify additional
         * span processors, such as {@link BaggageSpanProcessor} which enables multi-span attributes.
         *
         * @param spanProcessor Instance of a {@link BaggageSpanProcessor} or custom SpanProcessor
         * @return builder
         */
        public Builder addSpanProcessor(SpanProcessor spanProcessor) {
            this.additionalSpanProcessors.add(spanProcessor);
            return this;
        }

        /**
         * Add a string attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return AttributesBuilder
         */
        public AttributesBuilder addResourceAttribute(String key, String value) {
            return this.resourceAttributes.put(key, value);
        }

        /**
         * Add a long attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return AttributesBuilder
         */
        public AttributesBuilder addResourceAttribute(String key, long value) {
            return this.resourceAttributes.put(key, value);
        }

        /**
         * Add a double attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return AttributesBuilder
         */
        public AttributesBuilder addResourceAttribute(String key, double value) {
            return this.resourceAttributes.put(key, value);
        }

        /**
         * Add a boolean attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return AttributesBuilder
         */
        public AttributesBuilder addResourceAttribute(String key, boolean value) {
            return this.resourceAttributes.put(key, value);
        }

        /**
         * Add a String array attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return AttributesBuilder
         */
        public AttributesBuilder addResourceAttribute(String key, String... value) {
            return this.resourceAttributes.put(key, value);
        }

        /**
         * Returns a new {@link HoneycombSdk} built with the configuration of this {@link
         * Builder} and registers it as the global {@link
         * io.opentelemetry.api.OpenTelemetry}. An exception will be thrown if this method is attempted to
         * be called multiple times in the lifecycle of an application - ensure you have only one SDK for
         * use as the global instance. If you need to configure multiple SDKs for tests, use {@link
         * GlobalOpenTelemetry#resetForTest()} between them.
         *
         * @return {@link HoneycombSdk} instance
         * @see GlobalOpenTelemetry
         */
        public HoneycombSdk buildAndRegisterGlobal() {
            HoneycombSdk sdk = build();
            GlobalOpenTelemetry.set(sdk);
            return sdk;
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
         * @return {@link HoneycombSdk} instance
         * @see GlobalOpenTelemetry
         */
        public HoneycombSdk build() {
            Preconditions.checkNotNull(sampler, "sampler must be non-null");

            Logger logger = Logger.getLogger(HoneycombSdk.class.getName());

            if (!isPresent(tracesApiKey)) {
                logger.warning(EnvironmentConfiguration.getErrorMessage("API key",
                    EnvironmentConfiguration.HONEYCOMB_API_KEY));
            }
            if (!isPresent(tracesDataset)) {
                logger.warning(EnvironmentConfiguration.getErrorMessage("dataset",
                    EnvironmentConfiguration.HONEYCOMB_DATASET));
            }

            OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();

            if (tracesEndpoint != null) {
                builder.setEndpoint(tracesEndpoint);
            } else {
                builder.setEndpoint(EnvironmentConfiguration.DEFAULT_HONEYCOMB_ENDPOINT);
            }

            if (isPresent(tracesApiKey) && isPresent(tracesDataset)) {
                builder
                    .addHeader(EnvironmentConfiguration.HONEYCOMB_TEAM_HEADER, tracesApiKey)
                    .addHeader(EnvironmentConfiguration.HONEYCOMB_DATASET_HEADER, tracesDataset);
            }
            SpanExporter exporter = builder.build();

            SdkTracerProviderBuilder tracerProviderBuilder = SdkTracerProvider.builder().setSampler(sampler)
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build());

            this.additionalSpanProcessors.forEach(tracerProviderBuilder::addSpanProcessor);

            DistroMetadata.getMetadata().forEach(resourceAttributes::put);
            if (StringUtils.isNotEmpty(serviceName)) {
                resourceAttributes.put(EnvironmentConfiguration.SERVICE_NAME_FIELD, serviceName);
            }
            tracerProviderBuilder.setResource(
                Resource.create(resourceAttributes.build()));

            if (propagators == null) {
                propagators = ContextPropagators.create(
                    TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(),
                        W3CBaggagePropagator.getInstance()));
            }

            EnvironmentConfiguration.enableOTLPMetrics(metricsEndpoint, metricsApiKey, metricsDataset);
            return new HoneycombSdk(tracerProviderBuilder.build(), propagators);
        }
    }

    /**
     * This class allows the SDK to unobfuscate an obfuscated static global provider.
     *
     * <p>Static global providers are obfuscated when they are returned from the API to prevent users
     * from casting them to their SDK specific implementation. For example, we do not want users to
     * use patterns like {@code (TracerSdkProvider) OpenTelemetry.getGlobalTracerProvider()}.</p>
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
