package io.honeycomb.opentelemetry;

import java.util.HashMap;
import java.util.Map;

/**
 * DistroMetadata is a container for metadata that we'd like to include in all
 * Honeycomb OpenTelemetry distributions. The fields defined in this help the
 * user see what version of telemetry was used to capture data, which will be
 * useful in debugging issues that come up from version mismatches, etc.
 */
public class DistroMetadata {

    public static final String VERSION_FIELD = "honeycomb.distro.version";
    /**
     * Note that we are intentionally duplicating this from the Gradle build
     * file. Normally we'd use processResources to produce a properties file
     * with the version number and read it, but that isn't possible from the
     * Java agent.
     */
    public static final String VERSION_VALUE = "1.6.0";
    public static final String VARIANT_FIELD = "honeycomb.distro.variant";
    public static final String VARIANT_AGENT = "agent";
    public static final String VARIANT_SDK = "sdk";
    public static final String RUNTIME_VERSION_FIELD = "honeycomb.distro.runtime_version";
    public static final String RUNTIME_VERSION_VALUE = System.getProperty("java.runtime.version");

    public static final String OTLP_PROTO_VERSION_HEADER = "x-otlp-version";
    public static final String OTLP_PROTO_VERSION_VALUE = "0.20.0";

    /**
     * Get Metadata as a map of strings to strings.
     *
     * @return map of metadata to include as resource attributes.
     * @deprecated use getAgentMetadata() or getSDKMetadata() instead
     */
    public static Map<String, String> getMetadata() {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(VERSION_FIELD, VERSION_VALUE);
        metadata.put(RUNTIME_VERSION_FIELD, RUNTIME_VERSION_VALUE);
        return metadata;
    }

    public static Map<String, String> getAgentMetadata() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(VERSION_FIELD, VERSION_VALUE);
        metadata.put(RUNTIME_VERSION_FIELD, RUNTIME_VERSION_VALUE);
        metadata.put(VARIANT_FIELD, VARIANT_AGENT);
        return metadata;
    }

    public static Map<String, String> getSDKMetadata() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(VERSION_FIELD, VERSION_VALUE);
        metadata.put(RUNTIME_VERSION_FIELD, RUNTIME_VERSION_VALUE);
        metadata.put(VARIANT_FIELD, VARIANT_SDK);
        return metadata;
    }
}
