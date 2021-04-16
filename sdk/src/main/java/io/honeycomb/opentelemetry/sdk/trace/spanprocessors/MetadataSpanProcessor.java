package io.honeycomb.opentelemetry.sdk.trace.spanprocessors;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MetadataSpanProcessor implements SpanProcessor {

    private final String HONEYCOMB_SDK_VERSION_ATTRIBUTE = "honeycomb.meta.sdk.version";
    private final String HONEYCOMB_SDK_VERSION_PREFIX = "Honeycomb-OpenTelemetry-Java";
    private final Map<String, String> metadataCache = new HashMap<String, String>();

    private String getVersion() {
        if (metadataCache.containsKey("version")) {
            return metadataCache.get("version");
        }

        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("sdk.properties"));
        } catch (IOException e) {
            return "unknown-version";
        }

        return properties.getProperty("version");
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        span.setAttribute(
            HONEYCOMB_SDK_VERSION_ATTRIBUTE,
            HONEYCOMB_SDK_VERSION_PREFIX + " " + getVersion());
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
