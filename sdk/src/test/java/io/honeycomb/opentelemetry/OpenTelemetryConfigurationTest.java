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

            Field providerField = sdkProvider.getClass().getDeclaredField("sharedState");
            providerField.setAccessible(true);
            Object sharedState = providerField.get(sdkProvider);
            Field resourceField = sharedState.getClass().getDeclaredField("resource");
            resourceField.setAccessible(true);
            Resource r = (Resource) resourceField.get(sharedState);
            Attributes attrs = r.getAttributes();

            // verify resource attributes
            Assertions.assertEquals("0.6.1", attrs.get(AttributeKey.stringKey("honeycomb.distro.version")));
            Assertions.assertEquals(System.getProperty("java.runtime.version"), attrs.get(AttributeKey.stringKey("honeycomb.distro.runtime_version")));
            Assertions.assertEquals("str", attrs.get(AttributeKey.stringKey("str")));
            Assertions.assertEquals(true, attrs.get(AttributeKey.booleanKey("bool")));
            Assertions.assertEquals(123, attrs.get(AttributeKey.longKey("int")));
            Assertions.assertEquals(Arrays.asList("str1", "str2", "str3"), attrs.get(AttributeKey.stringArrayKey("str-array")));

            Field procesorField = sharedState.getClass().getDeclaredField("activeSpanProcessor");
            procesorField.setAccessible(true);

            Object sp = procesorField.get(sharedState);
            Field allSpanProcessors = sp.getClass().getDeclaredField("spanProcessorsAll");
            allSpanProcessors.setAccessible(true);
            List<SpanProcessor> processors = (List<SpanProcessor>) allSpanProcessors.get(sp);

            // verify configured span processors
            Assertions.assertInstanceOf(BaggageSpanProcessor.class, processors.get(0));
            Assertions.assertInstanceOf(BatchSpanProcessor.class, processors.get(1));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
