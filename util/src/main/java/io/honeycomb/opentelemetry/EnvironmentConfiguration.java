package io.honeycomb.opentelemetry;

/**
 * This is a utility class that helps read Honeycomb environment variables and system properties.
 * <p>
 * When both exist, system property value will take precedence over environment variable.
 */
public class EnvironmentConfiguration {

    public static final String HONEYCOMB_API_KEY = "HONEYCOMB_API_KEY";
    public static final String HONEYCOMB_API_ENDPOINT = "HONEYCOMB_API_ENDPOINT";
    public static final String HONEYCOMB_DATASET = "HONEYCOMB_DATASET";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SAMPLE_RATE = "SAMPLE_RATE";
    public static final String DEFAULT_HONEYCOMB_ENDPOINT = "https://api.honeycomb.io:443";
    public static final String SERVICE_NAME_FIELD = "service.name";

    /**
     * Reads the Honeycomb API key.
     *
     * @return honeycomb.api.key system property or HONEYCOMB_API_KEY environment variable
     */
    public static String getHoneycombApiKey() {
        return readVariable(HONEYCOMB_API_KEY, null);
    }

    /**
     * Read the Honeycomb API endpoint.
     *
     * @return honeycomb.api.endpoint system property or HONEYCOMB_API_ENDPOINT environment variable
     */
    public static String getHoneycombApiEndpoint() {
        return readVariable(HONEYCOMB_API_ENDPOINT, DEFAULT_HONEYCOMB_ENDPOINT);
    }

    /**
     * Read the Honeycomb dataset name.
     *
     * @return honeycomb.dataset system property or HONEYCOMB_DATASET environment variable
     */
    public static String getHoneycombDataset() {
        return readVariable(HONEYCOMB_DATASET, null);
    }

    /**
     * Read the service name.
     *
     * @return service.name system property or SERVICE_NAME environment variable
     */
    public static String getServiceName() {
        return readVariable(SERVICE_NAME, null);
    }

    /**
     * Read the sample rate.
     *
     * @return sample.rate system property or SAMPLE_RATE environment variable
     */
    public static int getSampleRate() throws NumberFormatException {
        final String sampleRate = readVariable(SAMPLE_RATE, "1");
        return Integer.parseInt(sampleRate);
    }

    /**
     * Get a friendly error message for missing variable.
     *
     * @param humanKey human-friendly variable description
     * @param key environment variable key
     * @return missing variable error message
     */
    public static String getErrorMessage(String humanKey, String key) {
        return String.format("Missing %s. Specify either %s environment variable or %s system property.",
            humanKey, key, getPropertyName(key));
    }

    private static String readVariable(String key, String fallback) {
        final String envValue = System.getenv(key);
        final String propValue = System.getProperty(getPropertyName(key));
        if (propValue != null) {
            return propValue;
        } else if (envValue != null) {
            return envValue;
        } else {
            return fallback;
        }
    }

    private static String getPropertyName(String envKey) {
        return envKey.toLowerCase().replace('_', '.');
    }
}
