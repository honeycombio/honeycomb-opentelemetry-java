package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class OpenTelemetryConfigurationTest {

    @Mock
    private Tracer tracer;

    @AfterEach
    public void tearDown() {
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    public void testConfiguration_tracerSettings() {
        Sampler sampler = new DeterministicTraceSampler(5);
        BaggageSpanProcessor baggageSpanProcessor = new BaggageSpanProcessor();
        OpenTelemetry openTelemetry = OpenTelemetryConfiguration.builder()
            .setSampler(sampler)
            .addSpanProcessor(baggageSpanProcessor)
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        // TODO:
        // Figure out a way to ensure that spans emitted with this
        // configuration have a sample.rate attribute with a value
        // of 5.
        Assertions.assertNotNull(sampler);
        Assertions.assertNotNull(baggageSpanProcessor);
        Assertions.assertNotNull(openTelemetry);
    }

    @Test
    void testConfiguration_headers() {
        OpenTelemetry openTelemetry = OpenTelemetryConfiguration.builder()
            .setApiKey("foobar")
            .setDataset("dataset")
            .build();

        Assertions.assertNotNull(openTelemetry);

        // TODO:
        // Figure out a way to test that the api key and dataset
        // have been set as headers in the Otlp Exporter and are
        // sent as gRPC metadata.
    }

    @Test
    void testConfiguration_addAttributes() {
        // NOTE: this does not verify the attributes are added correctly to
        // the resource, but does exercise the interface to ensure it is
        // can be used fluently (eg chain OpenTelemetryconfiguration.Builder calls)
        OpenTelemetry openTelemetry = OpenTelemetryConfiguration.builder()
            .addResourceAttribute("str", "str")
            .addResourceAttribute("bool", true)
            .addResourceAttribute("int", 123)
            .addResourceAttribute("str-array", "str1", "str2", "str3")
            .build();

        Assertions.assertNotNull(openTelemetry);
        // TODO: Figure out way to retrieve configured Resource from TracerProvider
        // to verify resource attributes have been added correctly
        // eg it might be possible using reflection
        // https://stackoverflow.com/questions/8267964/how-to-access-package-private-class-from-a-class-in-some-other-package
    }
}
