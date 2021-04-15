package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.sdk.autoconfigure.spi.SdkTracerProviderConfigurer;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;

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
        tracerProvider
            .setSampler(new DeterministicTraceSampler(sampleRate))
            .addSpanProcessor(new BaggageSpanProcessor());
    }
}
