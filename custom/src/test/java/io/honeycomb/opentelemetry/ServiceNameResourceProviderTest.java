package io.honeycomb.opentelemetry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.resources.Resource;

public class ServiceNameResourceProviderTest {

    @Mock
    private ConfigProperties configProperties;

    @Test
    public void test_ServiceNameResourceProvider_createResource_returns_configred_resource() {
        try {
            System.setProperty("service.name", "my-service");

            ServiceNameResourceProvider provider = new ServiceNameResourceProvider();
            Resource resource = provider.createResource(configProperties);

            Assertions.assertEquals(
                Attributes.of(
                    AttributeUtils.createStringAttribute("service.name"), "my-service"
                ),
                resource.getAttributes()
            );
        } finally {
            System.setProperty("service.name", "");
        }
    }
}
