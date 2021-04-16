package io.honeycomb.opentelemetry.sdk.trace.spanprocessors;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * MetadataSpanProcessor adds version information about the specific distribution
 * being used. This information is visible in a user's dataset and can be used to
 * determine variance in telemetry between versions, etc.
 */
public class MetadataSpanProcessor implements SpanProcessor {

    private final String HONEYCOMB_SDK_VERSION_ATTRIBUTE = "honeycomb.sdk.version";
    private final Map<String, String> metadataCache = new HashMap<String, String>();

    /**
     * Get version from properties file, storing it in an in-memory hashmap.
     */
    private String getVersion() {
        if (metadataCache.containsKey("honeycomb.sdk.version")) {
            return metadataCache.get("honeycomb.sdk.version");
        }

        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("sdk.properties"));
        } catch (IOException e) {
            return "unknown-version";
        }

        String version = properties.getProperty("honeycomb.sdk.version");
        metadataCache.put("honeycomb.sdk.version", version);
        return version;
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        span.setAttribute(
            HONEYCOMB_SDK_VERSION_ATTRIBUTE, getVersion());
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
