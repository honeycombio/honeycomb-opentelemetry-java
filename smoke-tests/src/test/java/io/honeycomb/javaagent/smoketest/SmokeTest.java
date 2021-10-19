package io.honeycomb.javaagent.smoketest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

abstract class SmokeTest {
    private static final Logger logger = LoggerFactory.getLogger(SmokeTest.class);
    private static final Network network = Network.newNetwork();

    // The a fake backend to receive OTLP data
    private static GenericContainer<?> backend;

    @BeforeAll
    static void startBackend() {
        backend =
            new GenericContainer<>(
                    "honeycombio/fake-otlp-backend:latest")
                .withExposedPorts(1234,5678)
                .withNetwork(network)
                .withNetworkAliases("backend")
                .withLogConsumer(new Slf4jLogConsumer(logger));
        backend.start();
    }

    @AfterAll
    static void stopBackend() {
        backend.stop();
    }
}
