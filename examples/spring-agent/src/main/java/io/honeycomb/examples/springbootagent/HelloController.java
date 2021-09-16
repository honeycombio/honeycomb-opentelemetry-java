package io.honeycomb.examples.springbootagent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;

@RestController
public class HelloController {
    private static final String importantInfo = "Important Information";

    @GetMapping("/")
      public String index() {
        Span span = Span.current();
        span.setAttribute("custom_field", "important value");
        String intro = getImportantInfo();
        String finalMessage = String.format("%s: Greetings from Spring Boot!", intro);
        return finalMessage;
	}

    @WithSpan("importantSpan")
    public String getImportantInfo() {
        return importantInfo;
    }
}
