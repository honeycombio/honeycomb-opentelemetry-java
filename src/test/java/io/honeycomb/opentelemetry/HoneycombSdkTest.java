package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class HoneycombSdkTest {

    @Mock
    private Tracer tracer;

    @AfterEach
    public void tearDown() {
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    public void testConfiguration_tracerSettings() {
        Sampler sampler = new DeterministicTraceSampler(5);
        HoneycombSdk honeycomb = new HoneycombSdk.Builder()
            .setSampler(sampler)
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        // TODO:
        // Figure out a way to ensure that spans emitted with this
        // configuration have a sample.rate attribute with a value
        // of 5.
        Assertions.assertNotNull(sampler);
        Assertions.assertNotNull(honeycomb);
    }

    @Test
    void testConfiguration_headers() {
        HoneycombSdk honeycomb = new HoneycombSdk.Builder()
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        Assertions.assertNotNull(honeycomb);

        // TODO:
        // Figure out a way to test that the api key and dataset
        // have been set as headers in the Otlp Exporter and are
        // sent as gRPC metadata.
    }

}
