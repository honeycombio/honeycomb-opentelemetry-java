package io.honeycomb.examples.springbootsdk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.honeycomb.opentelemetry.HoneycombSdk;
import org.springframework.beans.factory.annotation.Autowired;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@RestController
public class HelloController {
    private static final String importantInfo = "Important Information";

    @Autowired
    private HoneycombSdk sdk;

    @GetMapping("/")
    public String index() {
        Tracer tracer = sdk.getTracer("examples");
        Span span = tracer.spanBuilder("greetings").startSpan();
        span.setAttribute("custom_field", "important value");
        String intro = getImportantInfo();
        String finalMessage = String.format("%s: Greetings from Spring Boot!", intro);
        span.end();
        return finalMessage;
    }

    public String getImportantInfo() {
        return importantInfo;
    }

}
