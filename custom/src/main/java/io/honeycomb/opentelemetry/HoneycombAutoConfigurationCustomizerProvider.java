package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.samplers.Sampler;

/**
 * Honeycomb implementation of {@link AutoConfigurationCustomizerProvider} SPI.
 *
 * This configurer adds a trace sampler and a span processor to the OpenTelemetry auto-instrumentation.
 */
public class HoneycombAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {
    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration.addSamplerCustomizer(this::customizeSampler);
        autoConfiguration.addTracerProviderCustomizer(this::addBaggageSpanProcessor);
    }

    private Sampler customizeSampler(Sampler sampler, ConfigProperties configProperties) {
        int sampleRate;
        try {
            sampleRate = EnvironmentConfiguration.getSampleRate();
        } catch (NumberFormatException e) {
            System.err.println("WARN: Sample rate provided is not an integer, using default sample rate of 1");
            sampleRate = 1;
        }

        return new DeterministicTraceSampler(sampler, sampleRate);
    }

    private SdkTracerProviderBuilder addBaggageSpanProcessor(SdkTracerProviderBuilder tracerProvider, ConfigProperties config) {
        return tracerProvider.addSpanProcessor(new BaggageSpanProcessor());
    }
}
