package io.honeycomb.opentelemetry;

import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OpenTelemetryConfigurationTest {

    @AfterEach
    public void tearDown() {
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConfiguration_tracerSettings() {
        Sampler sampler = new DeterministicTraceSampler(5);
        OpenTelemetry openTelemetry = OpenTelemetryConfiguration.builder()
            .setSampler(sampler)
            .setApiKey("foobar")
            .setDataset("dataset")
            .addResourceAttribute("str", "str")
            .addResourceAttribute("bool", true)
            .addResourceAttribute("int", 123)
            .addResourceAttribute("str-array", "str1", "str2", "str3")
            .build();

        TracerProvider provider = openTelemetry.getTracerProvider();
        try {
            Method m = provider.getClass().getDeclaredMethod("unobfuscate");
            m.setAccessible(true);
            SdkTracerProvider sdkProvider = (SdkTracerProvider) m.invoke(provider, (Object[]) null);

            // verify sampler
            Sampler s = sdkProvider.getSampler();
            Assertions.assertEquals(sampler, s);

            Field providerField = pluckField(sdkProvider.getClass(), "sharedState");
            Object sharedState = providerField.get(sdkProvider);
            Field resourceField = pluckField(sharedState.getClass(), "resource");
            Resource r = (Resource) resourceField.get(sharedState);
            Attributes attrs = r.getAttributes();

            // verify resource attributes
            Assertions.assertEquals(DistroMetadata.VERSION_VALUE, attrs.get(AttributeKey.stringKey(DistroMetadata.VERSION_FIELD)));
            Assertions.assertEquals(DistroMetadata.RUNTIME_VERSION_VALUE, attrs.get(AttributeKey.stringKey(DistroMetadata.RUNTIME_VERSION_FIELD)));
            Assertions.assertEquals("str", attrs.get(AttributeKey.stringKey("str")));
            Assertions.assertEquals(true, attrs.get(AttributeKey.booleanKey("bool")));
            Assertions.assertEquals(123, attrs.get(AttributeKey.longKey("int")));
            Assertions.assertEquals(Arrays.asList("str1", "str2", "str3"), attrs.get(AttributeKey.stringArrayKey("str-array")));

            Field procesorField = pluckField(sharedState.getClass(), "activeSpanProcessor");

            Object sp = procesorField.get(sharedState);
            Field allSpanProcessors = pluckField(sp.getClass(), "spanProcessorsAll");
            List<SpanProcessor> processors = (List<SpanProcessor>) allSpanProcessors.get(sp);

            // verify configured span processors
            Assertions.assertInstanceOf(BaggageSpanProcessor.class, processors.get(0));
            Assertions.assertInstanceOf(BatchSpanProcessor.class, processors.get(1));
            SpanProcessor processor = processors.get(1);
            Assertions.assertTrue(processor.toString().contains("spanExporter=io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConfiguration_tracerSettings_httpExporter() {
        Sampler sampler = new DeterministicTraceSampler(5);
        OpenTelemetry openTelemetry = OpenTelemetryConfiguration.builder()
            .setOtlpProtocol("http/protobuf")
            .build();

        TracerProvider provider = openTelemetry.getTracerProvider();
        try {
            Method m = provider.getClass().getDeclaredMethod("unobfuscate");
            m.setAccessible(true);
            SdkTracerProvider sdkProvider = (SdkTracerProvider) m.invoke(provider, (Object[]) null);

            Field providerField = pluckField(sdkProvider.getClass(), "sharedState");
            Object sharedState = providerField.get(sdkProvider);

            Field procesorField = pluckField(sharedState.getClass(), "activeSpanProcessor");

            Object sp = procesorField.get(sharedState);
            Field allSpanProcessors = pluckField(sp.getClass(), "spanProcessorsAll");
            List<SpanProcessor> processors = (List<SpanProcessor>) allSpanProcessors.get(sp);

            // verify configured span processors
            Assertions.assertInstanceOf(BatchSpanProcessor.class, processors.get(1));
            SpanProcessor processor = processors.get(1);
            Assertions.assertTrue(processor.toString().contains("spanExporter=io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    private Field pluckField(Class aClass, String fieldName) throws NoSuchFieldException, SecurityException {
        Field pluckedField = aClass.getDeclaredField(fieldName);
        pluckedField.setAccessible(true);
        return pluckedField;
    }
}
