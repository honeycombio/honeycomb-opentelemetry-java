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

    protected static final String agentPath = System.getProperty("io.honeycomb.smoketest.agent.shadowJar.path");
    protected static final String agentOnlyAppPath = System.getProperty("io.honeycomb.smoketest.agentOnlyApp.bootJar.path");

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

    static void startAgentOnlyApp() {
        agentOnlyApp = new GenericContainer<>("openjdk:17-jdk-alpine")
            .withExposedPorts(5002)
            .waitingFor(Wait.forHttp("/").forPort(5002))
            .withNetwork(network)
            .withEnv("HONEYCOMB_API_ENDPOINT", "http://backend:1234")
            .withEnv("HONEYCOMB_API_KEY", "bogus_key")
            .withEnv("HONEYCOMB_DATASET", "bogus_dataset")
            .withCopyFileToContainer(MountableFile.forHostPath(agentPath), "/agent.jar")
            .withCopyFileToContainer(MountableFile.forHostPath(agentOnlyAppPath), "/app.jar")
            .withCommand("java -javaagent:/agent.jar -jar /app.jar");
        agentOnlyApp.start();
    }

    static void stopAgentOnlyApp() {
        agentOnlyApp.stop();
    }

    @AfterAll
    static void stopBackend() {
        backend.stop();
    }
}
