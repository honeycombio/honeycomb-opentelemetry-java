package io.honeycomb.examples.springbootsdk;

import java.util.Arrays;

import io.honeycomb.opentelemetry.OpenTelemetryConfiguration;
import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.OpenTelemetry;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
	@Bean
	public OpenTelemetry honeycomb() {
        return OpenTelemetryConfiguration.builder()
            .addSpanProcessor(new BaggageSpanProcessor())
            .setApiKey(System.getenv("HONEYCOMB_API_KEY"))
            .setDataset(System.getenv("HONEYCOMB_DATASET"))
            .setServiceName("example-service")
            .setEndpoint(System.getenv("HONEYCOMB_API_ENDPOINT"))
            .buildAndRegisterGlobal();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}
}
