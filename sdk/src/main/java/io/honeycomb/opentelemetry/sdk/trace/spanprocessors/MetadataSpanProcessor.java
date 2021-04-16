package io.honeycomb.opentelemetry.sdk.trace.spanprocessors;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.io.IOException;
import java.util.Properties;

public class MetadataSpanProcessor implements SpanProcessor {

    private final String HONEYCOMB_SDK_VERSION_ATTRIBUTE = "honeycomb.meta.sdk.version";
    private final String HONEYCOMB_SDK_VERSION_PREFIX = "Honeycomb-OpenTelemetry-Java";

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("sdk.properties"));
        } catch (IOException ignored) { }
        String version = properties.getProperty("version");
        span.setAttribute(HONEYCOMB_SDK_VERSION_ATTRIBUTE, HONEYCOMB_SDK_VERSION_PREFIX + " " + version);
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan span) {

    }

    @Override
    public boolean isEndRequired() {
        return false;
    }
}
