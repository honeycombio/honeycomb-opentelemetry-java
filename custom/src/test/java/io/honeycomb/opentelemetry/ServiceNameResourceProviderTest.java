package io.honeycomb.opentelemetry;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;

public class ServiceNameResourceProviderTest {

    @Before
    public void setup() {
        System.setProperty("service.name", "");
    }

    @Test
    public void test_ServiceNameResourceProvider_createResource_returns_configred_resource() {
        System.setProperty("service.name", "my-service");

        ServiceNameResourceProvider provider = new ServiceNameResourceProvider();
        Resource resource = provider.createResource(null);

        Assertions.assertEquals(
            Attributes.of(
                AttributeUtils.createStringAttribute("service.name"), "my-service"
            ),
            resource.getAttributes()
        );
    }
}
