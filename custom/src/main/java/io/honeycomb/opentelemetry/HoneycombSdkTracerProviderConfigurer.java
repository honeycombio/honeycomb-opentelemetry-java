package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.spi.SdkTracerProviderConfigurer;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Honeycomb implementation of {@link SdkTracerProviderConfigurer} SPI.
 *
 * This configurer adds a trace sampler and a span processor to the OpenTelemetry auto-instrumentation.
 */
public class HoneycombSdkTracerProviderConfigurer implements SdkTracerProviderConfigurer {
    @Override
    public void configure(SdkTracerProviderBuilder tracerProvider) {
        int sampleRate;
        try {
            sampleRate = EnvironmentConfiguration.getSampleRate();
        } catch (NumberFormatException e) {
            System.err.println("WARN: Sample rate provided is not an integer, using default sample rate of 1");
            sampleRate = 1;
        }

        AttributesBuilder builder = Attributes.builder();
        DistroMetadata.getMetadata().forEach(builder::put);
        String serviceName = EnvironmentConfiguration.getServiceName();
        if (StringUtils.isNotEmpty(serviceName)) {
            builder.put(EnvironmentConfiguration.SERVICE_NAME_FIELD, serviceName);
        }
        tracerProvider.setResource(
            Resource.create(builder.build()));

        tracerProvider
            .setSampler(new DeterministicTraceSampler(sampleRate))
            .addSpanProcessor(new BaggageSpanProcessor());
    }
}
