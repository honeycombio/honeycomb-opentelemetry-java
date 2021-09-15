# Examples

## Example Apps

To use the examples:

- Clone or download the entire repo, as they rely on local code instead of Maven Central
- Add environment variables (or system properties)
- From the spring-agent or spring-sdk directory run `../../gradlew bootRun`
- `curl localhost:8080` to get 'Important Information: Greetings from Spring Boot!'

### spring-agent

This example uses the agent along with the SDK for enriching the auto-instrumented data.

### spring-sdk

This example uses the Honeycomb SDK for manual instrumentation.
