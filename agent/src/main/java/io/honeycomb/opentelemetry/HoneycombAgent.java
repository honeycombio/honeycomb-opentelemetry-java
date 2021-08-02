package io.honeycomb.opentelemetry;

import io.opentelemetry.javaagent.OpenTelemetryAgent;

import java.lang.instrument.Instrumentation;

/**
 * Honeycomb wrapper around {@link OpenTelemetryAgent}.
 *
 * HoneycombAgent converts honeycomb environment configuration into OpenTelemetry configuration.
 */
public class HoneycombAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        configureEnvironment();
        OpenTelemetryAgent.premain(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        configureEnvironment();
        OpenTelemetryAgent.agentmain(agentArgs, inst);
    }

    private static void configureEnvironment() {

        final String apiKey = EnvironmentConfiguration.getHoneycombApiKey();
        final String apiEndpoint = EnvironmentConfiguration.getHoneycombApiEndpoint();
        final String dataset = EnvironmentConfiguration.getHoneycombDataset();

        if (apiKey == null) {
            System.out.printf("WARN: %s%n", EnvironmentConfiguration.getErrorMessage("API key", EnvironmentConfiguration.HONEYCOMB_API_KEY));
        }
        if (dataset == null) {
            System.out.printf("WARN: %s%n", EnvironmentConfiguration.getErrorMessage("dataset", EnvironmentConfiguration.HONEYCOMB_DATASET));
        }

        if (apiKey != null && dataset != null) {
            System.setProperty("otel.exporter.otlp.headers",
                String.format("%s=%s,%s=%s", EnvironmentConfiguration.HONEYCOMB_TEAM_HEADER, apiKey,
                    EnvironmentConfiguration.HONEYCOMB_DATASET_HEADER, dataset));
        }
        System.setProperty("otel.exporter.otlp.endpoint", apiEndpoint);
    }

}
