package io.honeycomb.opentelemetry;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentConfigurationTest {

    @BeforeEach
    public void setup() {
        System.setProperty("sample.rate", "");
        System.setProperty("otel.exporter.otlp.protocol", "");
        System.setProperty("service.name", "");
        System.setProperty("honeycomb.api.endpoint", "");
        System.setProperty("honeycomb.traces.endpoint", "");
        System.setProperty("honeycomb.metrics.endpoint", "");
        System.setProperty("honeycomb.api.key", "");
        System.setProperty("honeycomb.traces.apikey", "");
        System.setProperty("honeycomb.metrics.apikey", "");
        System.setProperty("honeycomb.dataset", "");
        System.setProperty("honeycomb.traces.dataset", "");
        System.setProperty("honeycomb.metrics.dataset", "");
        System.setProperty("honeycomb.config.file", "");
    }

    @Test
    public void test_dafault_sample_rate() {
        Assertions.assertEquals(1, EnvironmentConfiguration.getSampleRate());
    }

    @Test
    public void test_can_set_sample_rate() {
        System.setProperty("sample.rate", "10");
        Assertions.assertEquals(10, EnvironmentConfiguration.getSampleRate());
    }

    @Test
    public void test_dafault_otel_exporter_otlp_proto() {
        Assertions.assertEquals("grpc", EnvironmentConfiguration.getOtelExporterOtlpProtocol());
    }

    @Test
    public void test_can_set_otel_exporter_otlp_proto() {
        System.setProperty("otel.exporter.otlp.protocol", "http/protobuf");
        Assertions.assertEquals("http/protobuf", EnvironmentConfiguration.getOtelExporterOtlpProtocol());
    }

    @Test
    public void test_default_apiendpoint() {
        Assertions.assertEquals("https://api.honeycomb.io:443", EnvironmentConfiguration.getHoneycombApiEndpoint());
    }

    @Test
    public void test_can_set_service_name() {
        System.setProperty("service.name", "my-service");
        Assertions.assertEquals("my-service", EnvironmentConfiguration.getServiceName());
    }

    @Test
    public void test_traces_apikey() {
        System.setProperty("honeycomb.traces.apikey", "my-apikey");
        Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombTracesApiKey());
    }

    @Test
    public void test_traces_apikey_fallsback() {
        System.setProperty("honeycomb.api.key", "my-apikey");
        Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombTracesApiKey());
    }

    @Test
    public void test_traces_apiendpoint() {
        System.setProperty("honeycomb.traces.endpoint", "my-endpoint");
        Assertions.assertEquals("my-endpoint", EnvironmentConfiguration.getHoneycombTracesApiEndpoint());
    }

    @Test
    public void test_traces_apiendpoint_fallsback() {
        System.setProperty("honeycomb.api.endpoint", "my-endpoint");
        Assertions.assertEquals("my-endpoint", EnvironmentConfiguration.getHoneycombTracesApiEndpoint());
    }

    @Test
    public void test_traces_dataset() {
        System.setProperty("honeycomb.traces.dataset", "my-dataset");
        Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombTracesDataset());
    }

    @Test
    public void test_traces_dataset_fallsback() {
        System.setProperty("honeycomb.dataset", "my-dataset");
        Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombTracesDataset());
    }

    @Test
    public void test_metrics_apikey() {
        System.setProperty("honeycomb.metrics.apikey", "my-apikey");
        Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombMetricsApiKey());
    }

    @Test
    public void test_metrics_apikey_fallsback() {
        System.setProperty("honeycomb.api.key", "my-apikey");
        Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombMetricsApiKey());
    }

    @Test
    public void test_metrics_apiendpoint() {
        System.setProperty("honeycomb.metrics.endpoint", "my-apiendpoint");
        Assertions.assertEquals("my-apiendpoint", EnvironmentConfiguration.getHoneycombMetricsApiEndpoint());
    }

    @Test
    public void test_metrics_apiendpoint_fallsback() {
        System.setProperty("honeycomb.api.endpoint", "my-apiendpoint");
        Assertions.assertEquals("my-apiendpoint", EnvironmentConfiguration.getHoneycombMetricsApiEndpoint());
    }

    @Test
    public void test_metrics_dataset() {
        System.setProperty("honeycomb.metrics.dataset", "my-dataset");
        Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombMetricsDataset());
    }

    @Test
    public void test_metrics_dataset_does_not_fallback() {
        System.setProperty("honeycomb.dataset", "my-dataset");
        Assertions.assertEquals(null, EnvironmentConfiguration.getHoneycombMetricsDataset());
    }

    @Test
    public void test_enableOtlpTraces_sets_system_properties_for_legacy_key() {
        System.setProperty("honeycomb.api.key", "11111111111111111111111111111111");
        System.setProperty("honeycomb.dataset", "my-dataset");

        EnvironmentConfiguration.enableOTLPTraces();
        Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.traces.endpoint"));
        Assertions.assertEquals("x-otlp-version=0.16.0,X-Honeycomb-Team=11111111111111111111111111111111,X-Honeycomb-Dataset=my-dataset", System.getProperty("otel.exporter.otlp.traces.headers"));
    }

    @Test
    public void test_enableOtlpTraces_sets_system_properties_for_non_legacy_key() {
        System.setProperty("honeycomb.api.key", "specialenvkey");
        System.setProperty("honeycomb.dataset", "my-dataset");

        EnvironmentConfiguration.enableOTLPTraces();
        Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.traces.endpoint"));
        Assertions.assertEquals("x-otlp-version=0.16.0,X-Honeycomb-Team=specialenvkey", System.getProperty("otel.exporter.otlp.traces.headers"));
    }

    @Test
    public void test_enableOtlpMetrics_sets_system_properties() {
        System.setProperty("honeycomb.api.key", "my-key");
        System.setProperty("honeycomb.metrics.dataset", "my-dataset");

        EnvironmentConfiguration.enableOTLPMetrics();
        Assertions.assertEquals("otlp", System.getProperty("otel.metrics.exporter"));
        Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.metrics.endpoint"));
        Assertions.assertEquals("x-otlp-version=0.16.0,X-Honeycomb-Team=my-key,X-Honeycomb-Dataset=my-dataset", System.getProperty("otel.exporter.otlp.metrics.headers"));
    }

    // make sure OtlpTraces logic doesn't bleed into metrics; dataset still needed
    @Test
    public void test_enableOtlpMetrics_sets_system_properties_for_legacy_key() {
        System.setProperty("honeycomb.api.key", "11111111111111111111111111111111");
        System.setProperty("honeycomb.metrics.dataset", "my-dataset");

        EnvironmentConfiguration.enableOTLPMetrics();
        Assertions.assertEquals("otlp", System.getProperty("otel.metrics.exporter"));
        Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.metrics.endpoint"));
        Assertions.assertEquals("x-otlp-version=0.16.0,X-Honeycomb-Team=11111111111111111111111111111111,X-Honeycomb-Dataset=my-dataset", System.getProperty("otel.exporter.otlp.metrics.headers"));
    }

    @Test
    public void test_enableOtlpMetrics_without_dataset_does_not_enable_metrics() {
        System.setProperty("honeycomb.api.key", "my-key");
        System.setProperty("honeycomb.metrics.dataset", "");

        EnvironmentConfiguration.enableOTLPMetrics();
        Assertions.assertEquals(null, System.getProperty("otel.metrics.exporter"));
        Assertions.assertEquals(null, System.getProperty("otel.exporter.otlp.metrics.endpoint"));
        Assertions.assertEquals(null, System.getProperty("otel.exporter.otlp.metrics.headers"));
    }

    @Test
    public void test_loadPropertiesFromConfigFile_sets_system_properties() {
        try {
            final File file = File.createTempFile("app", "properties");
            file.deleteOnExit();
            try (FileWriter fs = new FileWriter(file)) {
                fs.write("honeycomb.api.key=my-key");
            }
            System.setProperty("honeycomb.config.file", file.getAbsolutePath());

            Properties properties = EnvironmentConfiguration.loadPropertiesFromConfigFile();
            Assertions.assertEquals("my-key", properties.getProperty("honeycomb.api.key"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
