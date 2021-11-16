package io.honeycomb.opentelemetry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is a utility class that helps read Honeycomb environment variables and system properties.
 * <p>
 * When both exist, system property value will take precedence over environment variable.
 */
public class EnvironmentConfiguration {

    public static final String HONEYCOMB_API_KEY = "HONEYCOMB_API_KEY";
    public static final String HONEYCOMB_TRACES_APIKEY = "HONEYCOMB_TRACES_APIKEY";
    public static final String HONEYCOMB_METRICS_APIKEY = "HONEYCOMB_METRICS_APIKEY";
    public static final String HONEYCOMB_API_ENDPOINT = "HONEYCOMB_API_ENDPOINT";
    public static final String HONEYCOMB_TRACES_ENDPOINT = "HONEYCOMB_TRACES_ENDPOINT";
    public static final String HONEYCOMB_METRICS_ENDPOINT = "HONEYCOMB_METRICS_ENDPOINT";
    public static final String HONEYCOMB_DATASET = "HONEYCOMB_DATASET";
    public static final String HONEYCOMB_TRACES_DATASET = "HONEYCOMB_TRACES_DATASET";
    public static final String HONEYCOMB_METRICS_DATASET = "HONEYCOMB_METRICS_DATASET";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SAMPLE_RATE = "SAMPLE_RATE";
    public static final String DEFAULT_HONEYCOMB_ENDPOINT = "https://api.honeycomb.io:443";
    public static final String SERVICE_NAME_FIELD = "service.name";
    public static final String HONEYCOMB_TEAM_HEADER = "X-Honeycomb-Team";
    public static final String HONEYCOMB_DATASET_HEADER = "X-Honeycomb-Dataset";
    public static final String HONEYCOMB_CONFIGURATION_FILE = "HONEYCOMB_CONFIG_FILE";

    private static final Properties properties = loadPropertiesFromConfigFile();
    private static final String OTEL_AGENT_CONFIG_FILE = "otel.javaagent.configuration-file";

    /**
     * Reads the Honeycomb API key.
     *
     * @return honeycomb.api.key system property or HONEYCOMB_API_KEY environment variable
     */
    public static String getHoneycombApiKey() {
        return readVariable(HONEYCOMB_API_KEY, null);
    }

    /**
     * Reads the Honeycomb API key to send trace data.
     *
     * @return honeycomb.traces.apikey system property or HONEYCOMB_TRACES_APIKEY environment variable
     */
    public static String getHoneycombTracesApiKey() {
        return readVariable(HONEYCOMB_TRACES_APIKEY, getHoneycombApiKey());
    }

    /**
     * Reads the Honeycomb API key to send metrics data.
     *
     * @return honeycomb.metrics.apikey system property or HONEYCOMB_METRICS_APIKEY environment variable
     */
    public static String getHoneycombMetricsApiKey() {
        return readVariable(HONEYCOMB_METRICS_APIKEY, getHoneycombApiKey());
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
     * Reads the Honeycomb API endpoint to send trace data.
     *
     * @return honeycomb.traces.endpoint system property or HONEYCOMB_TRACES_ENDPOINT environment variable
     */
    public static String getHoneycombTracesApiEndpoint() {
        return readVariable(HONEYCOMB_TRACES_ENDPOINT, getHoneycombApiEndpoint());
    }

    /**
     * Reads the Honeycomb API endpoint to send metrics data.
     *
     * @return honeycomb.metrics.endpoint system property or HONEYCOMB_METRICS_ENDPOINT environment variable
     */
    public static String getHoneycombMetricsApiEndpoint() {
        return readVariable(HONEYCOMB_METRICS_ENDPOINT, getHoneycombApiEndpoint());
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
     * Read the Honeycomb dataset to store trace data.
     *
     * @return honeycomb.traces.dataset system property or HONEYCOMB_TRACES_DATASET environment variable
     */
    public static String getHoneycombTracesDataset() {
        return readVariable(HONEYCOMB_TRACES_DATASET, getHoneycombDataset());
    }

    /**
     * Read the Honeycomb dataset to store metrics data.
     *
     * @return honeycomb.metrics.dataset system property or HONEYCOMB_METRICS_DATASET environment variable
     */
    public static String getHoneycombMetricsDataset() {
        return readVariable(HONEYCOMB_METRICS_DATASET, null); // don't default to generic dataset like we do for apikey and endpoint
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
     * @param key      environment variable key
     * @return missing variable error message
     */
    public static String getErrorMessage(String humanKey, String key) {
        return String.format("Missing %s. Specify either %s environment variable or %s system property.",
            humanKey, key, getPropertyName(key));
    }

    public static boolean isPresent(String value) {
        return value != null && !value.isEmpty();
    }

    private static String readVariable(String key, String fallback) {
        String propertyName = getPropertyName(key);
        String value;
        if (propertyName != null) {
            value = properties.getProperty(propertyName);
            if (isPresent(value)) {
                return value;
            }
        }
        value = System.getProperty(propertyName);
        if (isPresent(value)) {
            return value;
        }
        value = System.getenv(key);
        if (isPresent(value)) {
            return value;
        }
        return fallback;
    }

    private static String getPropertyName(String envKey) {
        return envKey.toLowerCase().replace('_', '.');
    }

    public static void enableOTLPTraces() {
        final String endpoint = getHoneycombTracesApiEndpoint();
        final String apiKey = getHoneycombTracesApiKey();
        final String dataset = getHoneycombTracesDataset();

        if (!isPresent(apiKey)) {
            System.out.printf("WARN: %s%n", getErrorMessage("API key", HONEYCOMB_API_KEY));
        }
        if (!isPresent(dataset)) {
            System.out.printf("WARN: %s%n", getErrorMessage("dataset", HONEYCOMB_DATASET));
        }

        System.setProperty("otel.exporter.otlp.traces.endpoint", endpoint);
        System.setProperty("otel.exporter.otlp.traces.headers",
            String.format("%s=%s,%s=%s",
                HONEYCOMB_TEAM_HEADER, apiKey,
                HONEYCOMB_DATASET_HEADER, dataset));
    }

    public static void enableOTLPMetrics() {
        final String endpoint = getHoneycombMetricsApiEndpoint();
        final String apiKey = getHoneycombMetricsApiKey();
        final String dataset = getHoneycombMetricsDataset();

        if (isPresent(dataset)) {
            System.setProperty("otel.metrics.exporter", "otlp");
            System.setProperty("otel.exporter.otlp.metrics.endpoint", endpoint);
            System.setProperty("otel.exporter.otlp.metrics.headers",
                    String.format("%s=%s,%s=%s",
                    HONEYCOMB_TEAM_HEADER, apiKey,
                    HONEYCOMB_DATASET_HEADER, dataset));
        }
    }

    public static Properties loadPropertiesFromConfigFile() {
        // check system property then env var for properties file path
        // we can't use readVariable here because it uses properties
        String path = System.getProperty(getPropertyName(HONEYCOMB_CONFIGURATION_FILE));
        if (!isPresent(path)) {
            path = System.getenv(HONEYCOMB_CONFIGURATION_FILE);
        }

        Properties properties = new Properties();
        if (isPresent(path)) {
            try (InputStream stream = new FileInputStream(path)) {
                properties.load(stream);

                // if the otel config path has not been set, set it to the honeycomb config path
                if (!isPresent(System.getProperty(OTEL_AGENT_CONFIG_FILE))) {
                    System.setProperty(OTEL_AGENT_CONFIG_FILE, path);
                }
            } catch (Exception e) {
                System.out.println("Failed to load config file with path: " + path);
                System.out.print(e);
            }
        }

        return properties;
    }
}
