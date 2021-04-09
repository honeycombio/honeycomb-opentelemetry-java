package io.honeycomb.opentelemetry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class HoneycombSdkTest {
    @Mock
    private SdkTracerProvider tracerProvider;
    @Mock
    private ContextPropagators propagators;

    @AfterEach
    void tearDown() {
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    void testConfiguration_tracerSettings() {
        Sampler sampler = new DeterministicTraceSampler(5);
        HoneycombSdk honeycomb = new HoneycombSdk.Builder()
            .setSampler(sampler)
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        TracerProvider unobfuscatedTracerProvider =
            ((HoneycombSdk.ObfuscatedTracerProvider) honeycomb.getTracerProvider()).unobfuscate();

        assertThat(unobfuscatedTracerProvider)
            .isInstanceOfSatisfying(
                SdkTracerProvider.class,
                sdkTracerProvider ->
                    assertThat(
                        sdkTracerProvider.getSampler()
                            .getDescription()).isEqualTo(sampler.getDescription()));

    }

    @Test
    void testConfiguration_defaultSampler() {
        HoneycombSdk honeycomb = new HoneycombSdk.Builder()
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        TracerProvider unobfuscatedTracerProvider =
            ((HoneycombSdk.ObfuscatedTracerProvider) honeycomb.getTracerProvider()).unobfuscate();

        assertThat(unobfuscatedTracerProvider)
            .isInstanceOfSatisfying(
                SdkTracerProvider.class,
                sdkTracerProvider ->
                    assertThat(
                        sdkTracerProvider.getSampler()
                            .getDescription()).isEqualTo(Sampler.alwaysOn().getDescription()));
    }

}
