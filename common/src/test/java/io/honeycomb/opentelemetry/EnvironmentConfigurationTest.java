package io.honeycomb.opentelemetry;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnvironmentConfigurationTest {

    @Test
    public void test_dafault_sample_rate() {
        System.setProperty("sample.rate", "");
        Assertions.assertEquals(1, EnvironmentConfiguration.getSampleRate());
    }

    @Test
    public void test_can_set_sample_rate() {
        try {
            System.setProperty("sample.rate", "10");
            Assertions.assertEquals(10, EnvironmentConfiguration.getSampleRate());
        } finally {
            System.setProperty("sample.rate", "");
        }
    }

    @Test
    public void test_default_apiendpoint() {
        try {
            System.setProperty("honeycomb.api.endpoint", "");
            Assertions.assertEquals("https://api.honeycomb.io:443", EnvironmentConfiguration.getHoneycombApiEndpoint());
        } finally {
            System.setProperty("honeycomb.api.endpoint", "");
        }
    }

    @Test
    public void test_can_set_service_name() {
        try {
            System.setProperty("service.name", "my-service");
            Assertions.assertEquals("my-service", EnvironmentConfiguration.getServiceName());
        } finally {
            System.setProperty("honeycomb.api.endpoint", "");
        }
    }

    @Test
    public void test_traces_apikey() {
        try {
            System.setProperty("honeycomb.traces.apikey", "my-apikey");
            Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombTracesApiKey());
        } finally {
            System.setProperty("honeycomb.traces.apikey", "");
        }
    }

    @Test
    public void test_traces_apikey_fallsback() {
        try {
            System.setProperty("honeycomb.api.key", "my-apikey");
            Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombTracesApiKey());
        } finally {
            System.setProperty("honeycomb.api.key", "");
        }
    }

    @Test
    public void test_traces_apiendpoint() {
        try {
            System.setProperty("honeycomb.traces.endpoint", "my-endpoint");
            Assertions.assertEquals("my-endpoint", EnvironmentConfiguration.getHoneycombTracesApiEndpoint());
        } finally {
            System.setProperty("honeycomb.traces.endpoint", "");
        }
    }

    @Test
    public void test_traces_apiendpoint_fallsback() {
        try {
            System.setProperty("honeycomb.api.endpoint", "my-endpoint");
            Assertions.assertEquals("my-endpoint", EnvironmentConfiguration.getHoneycombTracesApiEndpoint());
        } finally {
            System.setProperty("honeycomb.api.endpoint", "");
        }
    }

    @Test
    public void test_traces_dataset() {
        try {
            System.setProperty("honeycomb.traces.dataset", "my-dataset");
            Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombTracesDataset());
        } finally {
            System.setProperty("honeycomb.traces.dataset", "");
        }
    }

    @Test
    public void test_traces_dataset_fallsback() {
        try {
            System.setProperty("honeycomb.dataset", "my-dataset");
            Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombTracesDataset());
        } finally {
            System.setProperty("honeycomb.dataset", "");
        }
    }

    @Test
    public void test_metrics_apikey() {
        try {
            System.setProperty("honeycomb.metrics.apikey", "my-apikey");
            Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombMetricsApiKey());
        } finally {
            System.setProperty("honeycomb.metrics.apikey", "");
        }
    }

    @Test
    public void test_metrics_apikey_fallsback() {
        try {
            System.setProperty("honeycomb.api.key", "my-apikey");
            Assertions.assertEquals("my-apikey", EnvironmentConfiguration.getHoneycombMetricsApiKey());
        } finally {
            System.setProperty("honeycomb.api.key", "");
        }
    }

    @Test
    public void test_metrics_apiendpoint() {
        try {
            System.setProperty("honeycomb.metrics.endpoint", "my-apiendpoint");
            Assertions.assertEquals("my-apiendpoint", EnvironmentConfiguration.getHoneycombMetricsApiEndpoint());
        } finally {
            System.setProperty("honeycomb.metrics.endpoint", "");
        }
    }

    @Test
    public void test_metrics_apiendpoint_fallsback() {
        try {
            System.setProperty("honeycomb.api.endpoint", "my-apiendpoint");
            Assertions.assertEquals("my-apiendpoint", EnvironmentConfiguration.getHoneycombMetricsApiEndpoint());
        } finally {
            System.setProperty("honeycomb.api.endpoint", "");
        }
    }

    @Test
    public void test_metrics_dataset() {
        try {
            System.setProperty("honeycomb.metrics.dataset", "my-dataset");
            Assertions.assertEquals("my-dataset", EnvironmentConfiguration.getHoneycombMetricsDataset());
        } finally {
            System.setProperty("honeycomb.metrics.dataset", "");
        }
    }

    @Test
    public void test_metrics_dataset_does_not_fallback() {
        try {
            System.setProperty("honeycomb.dataset", "my-dataset");
            Assertions.assertEquals(null, EnvironmentConfiguration.getHoneycombMetricsDataset());
        } finally {
            System.setProperty("honeycomb.dataset", "");
        }
    }

    @Test
    public void test_enableOtlpTraces_sets_system_properties() {
        try {
            System.setProperty("honeycomb.api.key", "my-key");
            System.setProperty("honeycomb.dataset", "my-dataset");

            EnvironmentConfiguration.enableOTLPTraces();
            Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.traces.endpoint"));
            Assertions.assertEquals("X-Honeycomb-Team=my-key,X-Honeycomb-Dataset=my-dataset", System.getProperty("otel.exporter.otlp.traces.headers"));
        } finally {
            System.setProperty("honeycomb.api.key", "");
            System.setProperty("honeycomb.dataset", "");
        }
    }

    @Test
    public void test_enableOtlpMetrics_sets_system_properties() {
        try {
            System.setProperty("honeycomb.api.key", "my-key");
            System.setProperty("honeycomb.metrics.dataset", "my-dataset");

            EnvironmentConfiguration.enableOTLPMetrics();
            Assertions.assertEquals("otlp", System.getProperty("otel.metrics.exporter"));
            Assertions.assertEquals("https://api.honeycomb.io:443", System.getProperty("otel.exporter.otlp.metrics.endpoint"));
            Assertions.assertEquals("X-Honeycomb-Team=my-key,X-Honeycomb-Dataset=my-dataset", System.getProperty("otel.exporter.otlp.metrics.headers"));
        } finally {
            System.setProperty("honeycomb.api.key", "");
            System.setProperty("honeycomb.dataset", "");
        }
    }

    @Test
    public void test_enableOtlpMetrics_without_dataset_does_not_enable_metrics() {
        try {
            System.setProperty("honeycomb.api.key", "my-key");
            System.setProperty("honeycomb.metrics.dataset", "");

            EnvironmentConfiguration.enableOTLPMetrics();
            Assertions.assertEquals(null, System.getProperty("otel.metrics.exporter"));
            Assertions.assertEquals(null, System.getProperty("otel.exporter.otlp.metrics.endpoint"));
            Assertions.assertEquals(null, System.getProperty("otel.exporter.otlp.metrics.headers"));
        } finally {
            System.setProperty("honeycomb.api.key", "");
            System.setProperty("honeycomb.metrics.dataset", "");
        }
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
        } finally {
            System.setProperty("honeycomb.config.file", "");
        }
    }
}
