package io.honeycomb.examples.springbootsdk;

import io.honeycomb.opentelemetry.OpenTelemetryConfiguration;
import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.sdk.OpenTelemetrySdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Bean
    public OpenTelemetrySdk honeycomb() {
        return OpenTelemetryConfiguration.builder()
            // add baggage span processor that copies baggage attributes to new spans
            .addSpanProcessor(new BaggageSpanProcessor())
            // set deterministic trace sampler with sample rate of 1/1 (100%)
            .setSampler(new DeterministicTraceSampler(1))
            .setApiKey(System.getenv("HONEYCOMB_API_KEY"))
            .setDataset(System.getenv("HONEYCOMB_DATASET"))
            .setServiceName(System.getenv("SERVICE_NAME"))
            .setEndpoint(System.getenv("HONEYCOMB_API_ENDPOINT"))
            .buildAndRegisterGlobal();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("OK I'm ready now!");
    }
}
