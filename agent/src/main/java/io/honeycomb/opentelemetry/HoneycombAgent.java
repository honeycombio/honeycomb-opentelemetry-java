package io.honeycomb.opentelemetry;

import io.opentelemetry.javaagent.OpenTelemetryAgent;

import java.lang.instrument.Instrumentation;

public class HoneycombAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            configureEnvironment();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        OpenTelemetryAgent.premain(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            configureEnvironment();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        OpenTelemetryAgent.agentmain(agentArgs, inst);
    }

    private static void configureEnvironment() throws Exception {
        final String apiKey = EnvironmentConfiguration.getHoneycombApiKey();
        final String apiEndpoint = EnvironmentConfiguration.getHoneycombApiEndpoint();
        final String dataset = EnvironmentConfiguration.getHoneycombDataset();
        final String serviceName = EnvironmentConfiguration.getServiceName();

        if (apiKey == null) {
            throw new Exception("WARN: Could not start Honeycomb agent: "
                + EnvironmentConfiguration.getErrorMessage("API key", EnvironmentConfiguration.HONEYCOMB_API_KEY));
        }
        if (dataset == null) {
            throw new Exception("WARN: Could not start Honeycomb agent: "
                + EnvironmentConfiguration.getErrorMessage("dataset", EnvironmentConfiguration.HONEYCOMB_DATASET));
        }

        System.setProperty("otel.exporter.otlp.headers",
            String.format("x-honeycomb-team=%s,x-honeycomb-dataset=%s", apiKey, dataset));
        System.setProperty("otel.exporter.otlp.endpoint", apiEndpoint);
        if (serviceName != null) {
            System.setProperty("otel.resource.attributes",
                String.format("service.name=%s", serviceName));
        }
    }

}
