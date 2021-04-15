package io.honeycomb.opentelemetry;

public class EnvironmentConfiguration {

    public static final String HONEYCOMB_API_KEY = "HONEYCOMB_API_KEY";
    public static final String HONEYCOMB_API_ENDPOINT = "HONEYCOMB_API_ENDPOINT";
    public static final String HONEYCOMB_DATASET = "HONEYCOMB_DATASET";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SAMPLE_RATE = "SAMPLE_RATE";
    public static final String DEFAULT_HONEYCOMB_ENDPOINT = "https://api.honeycomb.io:443";

    public static String getHoneycombApiKey() {
        return readVariable(HONEYCOMB_API_KEY, null);
    }

    public static String getHoneycombApiEndpoint() {
        return readVariable(HONEYCOMB_API_ENDPOINT, DEFAULT_HONEYCOMB_ENDPOINT);
    }

    public static String getHoneycombDataset() {
        return readVariable(HONEYCOMB_DATASET, null);
    }

    public static String getServiceName() {
        return readVariable(SERVICE_NAME, null);
    }

    public static int getSampleRate() throws NumberFormatException {
        final String sampleRate = readVariable(SAMPLE_RATE, "1");
        return Integer.parseInt(sampleRate);
    }

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
