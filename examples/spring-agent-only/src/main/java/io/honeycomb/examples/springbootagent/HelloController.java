package io.honeycomb.examples.springbootagentonly;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final String importantInfo = "Important Information";

    @GetMapping("/")
    public String index() {
        String intro = getImportantInfo();
        String finalMessage = String.format("%s: Greetings from Spring Boot!", intro);
        return finalMessage;
    }

    public String getImportantInfo() {
        return importantInfo;
    }
}
