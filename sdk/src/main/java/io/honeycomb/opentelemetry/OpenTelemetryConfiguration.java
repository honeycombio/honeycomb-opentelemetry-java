package io.honeycomb.opentelemetry;

import com.google.common.base.Preconditions;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.extension.resources.OsResource;
import io.opentelemetry.sdk.extension.resources.ProcessRuntimeResource;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static io.honeycomb.opentelemetry.EnvironmentConfiguration.isPresent;
import static io.honeycomb.opentelemetry.EnvironmentConfiguration.isLegacyKey;

/**
 * This class exists to make it easier and more intuitive to use Honeycomb with OpenTelemetry.
 */
public final class OpenTelemetryConfiguration {

    public static class Builder {
        private ContextPropagators propagators;
        private Sampler sampler = new DeterministicTraceSampler(1);
        private final List<SpanProcessor> additionalSpanProcessors = new ArrayList<SpanProcessor>() {{
            add(new BaggageSpanProcessor());
        }};
        private AttributesBuilder resourceAttributes = Attributes.builder();
        private Boolean enableDebug = false;

        private String tracesApiKey;
        private String tracesDataset;
        private String tracesEndpoint;
        private String serviceName;

        private Builder() {}

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
         * Sets the Honeycomb endpoint to use. Optional, defaults to the Honeycomb ingest API.
         *
         * @param endpoint a String to use as the endpoint URI. Must begin with https or http.
         * @return builder
         */
        public Builder setEndpoint(String endpoint) {
            setTracesEndpoint(endpoint);
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
         * <p>Note that if no sampler is specified, a {@link DeterministicTraceSampler} with a
         * sample rate of 1 (always sample) will be used by default.</p>
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
         * {@link BaggageSpanProcessor} and {@link BatchSpanProcessor} are always configured by default.
         *
         * @param spanProcessor Instance of a {@link SpanProcessor}.
         * @return builder
         */
        public Builder addSpanProcessor(SpanProcessor spanProcessor) {
            this.additionalSpanProcessors.add(spanProcessor);
            return this;
        }

        /**
         * Enables debug mode. When set to {@code true} a {@link LoggingSpanExporter} is added to the export pipeline
         * that will output traces and metrics data.
         *
         * @param enabled defaults to {@code false}
         * @return Builder
         */
        public Builder enableDebug(boolean enabled) {
            this.enableDebug = enabled;
            return this;
        }

        /**
         * Add a string attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return Builder
         */
        public Builder addResourceAttribute(String key, String value) {
            this.resourceAttributes.put(key, value);
            return this;
        }

        /**
         * Add a long attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return Builder
         */
        public Builder addResourceAttribute(String key, long value) {
            this.resourceAttributes.put(key, value);
            return this;
        }

        /**
         * Add a double attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return Builder
         */
        public Builder addResourceAttribute(String key, double value) {
            this.resourceAttributes.put(key, value);
            return this;
        }

        /**
         * Add a boolean attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return Builder
         */
        public Builder addResourceAttribute(String key, boolean value) {
            this.resourceAttributes.put(key, value);
            return this;
        }

        /**
         * Add a String array attribute as a resource attribute.
         *
         * @param key   The key to associate a value with
         * @param value The value to store as an attribute
         * @return Builder
         */
        public Builder addResourceAttribute(String key, String... value) {
            this.resourceAttributes.put(key, value);
            return this;
        }

        /**
         * Returns a new {@link OpenTelemetry} built with the configuration of this {@link
         * Builder} and registers it as the global {@link
         * io.opentelemetry.api.OpenTelemetry}. An exception will be thrown if this method is attempted to
         * be called multiple times in the lifecycle of an application - ensure you have only one SDK for
         * use as the global instance. If you need to configure multiple SDKs for tests, use {@link
         * GlobalOpenTelemetry#resetForTest()} between them.
         *
         * @return {@link OpenTelemetry} instance
         * @see GlobalOpenTelemetry
         */
        public OpenTelemetry buildAndRegisterGlobal() {
            OpenTelemetry sdk = build();
            GlobalOpenTelemetry.set(sdk);
            return sdk;
        }

        /**
         * Returns a new {@link OpenTelemetry} built with the configuration of this {@link
         * Builder}. This SDK is not registered as the global {@link
         * io.opentelemetry.api.OpenTelemetry}. It is recommended that you register one SDK using {@link
         * Builder#buildAndRegisterGlobal()} for use by instrumentation that requires
         * access to a global instance of {@link io.opentelemetry.api.OpenTelemetry}.
         *
         * <p>Note that the {@link Builder} automatically assigns a
         * {@link SdkTracerProvider} with a {@link BatchSpanProcessor} that has an
         * {@link OtlpGrpcSpanExporter} configured.</p>
         *
         * @return {@link OpenTelemetry} instance
         * @see GlobalOpenTelemetry
         */
        public OpenTelemetry build() {
            Preconditions.checkNotNull(sampler, "sampler must be non-null");

            Logger logger = Logger.getLogger(OpenTelemetryConfiguration.class.getName());

            // helpful to know if service name is missing
            if (!isPresent(serviceName)) {
                logger.warning(EnvironmentConfiguration.getErrorMessage("service name",
                    EnvironmentConfiguration.SERVICE_NAME) + " If left unset, this will show up in Honeycomb as unknown_service:java");
            }

            if (!isPresent(tracesApiKey)) {
                logger.warning(EnvironmentConfiguration.getErrorMessage("API key",
                    EnvironmentConfiguration.HONEYCOMB_API_KEY));
            }

            // heads up: even if dataset is set, it will be ignored
            if (isPresent(tracesApiKey) && !isLegacyKey(tracesApiKey) && isPresent(tracesDataset)) {
                if (isPresent(serviceName)) {
                    System.out.printf("WARN: Dataset is ignored in favor of service name. Data will be sent to service name: %s%n", serviceName);
                } else {
                    // should only get here if missing service name; above check shows "null" as service name
                    System.out.printf("WARN: Dataset is ignored in favor of service name.%n");
                }
            }

            // only warn on missing dataset if provided key is legacy or if no key is provided
            if (!isPresent(tracesDataset)) {
                if ((isPresent(tracesApiKey) && isLegacyKey(tracesApiKey)) || (!isPresent(tracesApiKey))) {
                    logger.warning(EnvironmentConfiguration.getErrorMessage("dataset",
                            EnvironmentConfiguration.HONEYCOMB_DATASET));
                }
            }

            OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();

            if (tracesEndpoint != null) {
                builder.setEndpoint(tracesEndpoint);
            } else {
                builder.setEndpoint(EnvironmentConfiguration.DEFAULT_HONEYCOMB_ENDPOINT);
            }

            // only add dataset if legacy key
            if (isPresent(tracesApiKey) && isLegacyKey(tracesApiKey) && isPresent(tracesDataset)) {
                    builder
                        .addHeader(EnvironmentConfiguration.HONEYCOMB_TEAM_HEADER, tracesApiKey)
                        .addHeader(EnvironmentConfiguration.HONEYCOMB_DATASET_HEADER, tracesDataset);
                }

            // otherwise add api key, ignore dataset if not legacy
            if (isPresent(tracesApiKey) && !isLegacyKey(tracesApiKey)) {
                builder
                    .addHeader(EnvironmentConfiguration.HONEYCOMB_TEAM_HEADER, tracesApiKey);
            }

            SpanExporter exporter = builder.build();

            SdkTracerProviderBuilder tracerProviderBuilder = SdkTracerProvider.builder()
                .setSampler(sampler);
            this.additionalSpanProcessors.forEach(tracerProviderBuilder::addSpanProcessor);

            if (this.enableDebug) {
                tracerProviderBuilder.addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter()));
            }
            tracerProviderBuilder.addSpanProcessor(BatchSpanProcessor.builder(exporter).build());

            DistroMetadata.getMetadata().forEach(resourceAttributes::put);
            if (StringUtils.isNotEmpty(serviceName)) {
                resourceAttributes.put(EnvironmentConfiguration.SERVICE_NAME_FIELD, serviceName);
            }
            tracerProviderBuilder.setResource(
                Resource.getDefault()
                    .merge(OsResource.get())
                    .merge(ProcessRuntimeResource.get())
                    .merge(Resource.create(resourceAttributes.build())));

            if (propagators == null) {
                propagators = ContextPropagators.create(
                    TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(),
                        W3CBaggagePropagator.getInstance()));
            }

            return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProviderBuilder.build())
                .setPropagators(propagators)
                .build();
        }
    }

    /**
     * Returns a new instance of a {@link Builder}.
     * @return {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
