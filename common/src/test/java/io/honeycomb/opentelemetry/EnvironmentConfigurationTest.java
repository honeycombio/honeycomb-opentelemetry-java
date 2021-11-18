package io.honeycomb.opentelemetry;

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

    // enableOtlpTraces
    // enableOtlpMetrics
    // loadPropertiesFromConfigFile
}
