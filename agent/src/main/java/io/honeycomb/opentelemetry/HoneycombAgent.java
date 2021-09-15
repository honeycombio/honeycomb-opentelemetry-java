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
        // set sytem properties to configre OTLP exporter to send traces & metrics data
        EnvironmentConfiguration.enableOTLPTraces();
        EnvironmentConfiguration.enableOTLPMetrics();
    }
}
