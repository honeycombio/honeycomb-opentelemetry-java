package io.honeycomb.examples.springbootsdk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;

@RestController
public class HelloController {
    private static final String importantInfo = "Important Information";

    @Autowired
    private OpenTelemetrySdk openTelemetry;

    @GetMapping("/")
    public String index() {
        Tracer tracer = openTelemetry.getTracer("examples");
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
