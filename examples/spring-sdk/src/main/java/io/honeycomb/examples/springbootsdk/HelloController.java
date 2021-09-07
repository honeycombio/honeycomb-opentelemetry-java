package io.honeycomb.examples.springbootsdk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.honeycomb.opentelemetry.HoneycombSdk;
import org.springframework.beans.factory.annotation.Autowired;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.extension.annotations.WithSpan;

@RestController
public class HelloController {

  @Autowired
  private HoneycombSdk sdk;

	@GetMapping("/")
    @WithSpan
	public String index() {
    Tracer tracer = sdk.getTracer("examples");
    Span span = tracer.spanBuilder("greetings").startSpan();
    span.end();
		return "Greetings from Spring Boot!";
	}

}
