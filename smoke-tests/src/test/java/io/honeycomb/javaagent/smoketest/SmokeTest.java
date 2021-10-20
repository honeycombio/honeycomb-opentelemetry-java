package io.honeycomb.javaagent.smoketest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

abstract class SmokeTest {
    private static final Logger logger = LoggerFactory.getLogger(SmokeTest.class);
    private static final Network network = Network.newNetwork();

    // CHECK THIS OUT FOR INSPO YAY
    // https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/examples/distro/smoke-tests/src/test/java/com/example/javaagent/smoketest

    // TODO - IS THIS WHERE THE JAR IS?
    protected static final String agentPath = System.getProperty("io.opentelemetry.smoketest.agent.shadowJar.path");

    // The a fake backend to receive OTLP data
    protected static GenericContainer<?> backend;

    @BeforeAll
    static void startBackend() {
        backend =
            new GenericContainer<>(
                    "honeycombio/fake-otlp-backend:latest")
                .withExposedPorts(1234,5678)
                .waitingFor(Wait.forHttp("/otlp-requests").forPort(5678))
                .withNetwork(network)
                .withNetworkAliases("backend")
                .withLogConsumer(new Slf4jLogConsumer(logger));
        backend.start();
    }

    protected static GenericContainer<?> agentOnlyApp;

    // TODO - WHATS THE REST?
    static void startAgentOnlyApp() {
        agentOnlyApp = new GenericContainer<>(
            "openjdk:17-jdk-alpine")
            .withCopyFileToContainer(
                MountableFile.forHostPath(agentPath), "/spring-agent-only*.jar")
    }

    @AfterAll
    static void stopBackend() {
        backend.stop();
    }
}
