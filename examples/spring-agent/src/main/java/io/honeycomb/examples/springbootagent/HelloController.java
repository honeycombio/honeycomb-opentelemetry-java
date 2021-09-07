package io.honeycomb.examples.springbootagent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.extension.annotations.WithSpan;

@RestController
public class HelloController {

    @GetMapping("/")
    @WithSpan // just adds an extra span for now
      public String index() {
        Span span = Span.current();
        span.setAttribute("custom_field", "important value");
		return "Greetings from Spring Boot!";
	}
}
