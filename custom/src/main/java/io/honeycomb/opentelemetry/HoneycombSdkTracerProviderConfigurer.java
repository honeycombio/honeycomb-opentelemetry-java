package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.spi.SdkTracerProviderConfigurer;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;

import java.io.InputStream;
import java.util.Properties;

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
        try {
            AttributesBuilder builder = Attributes.builder();
            final Properties properties = new Properties();
            InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("sdk.properties");
            System.out.println(input);
            if (input != null) {
                properties.load(input);
                properties.forEach((k, v) -> {
                    builder.put(k.toString(), v.toString());
                });
                tracerProvider.setResource(
                    Resource.create(builder.build()));
            }
        } catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
        }
        tracerProvider
            .setSampler(new DeterministicTraceSampler(sampleRate))
            .addSpanProcessor(new BaggageSpanProcessor());
    }
}
