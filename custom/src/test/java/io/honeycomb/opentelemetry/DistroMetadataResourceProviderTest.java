package io.honeycomb.opentelemetry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;

public class DistroMetadataResourceProviderTest {

    @Test
    public void test_DistroMetadataResourceProvider_createResource_returns_configred_resource() {
        DistroMetadataResourceProvider provider = new DistroMetadataResourceProvider();
        Resource resource = provider.createResource(null);

        Assertions.assertEquals(
            Attributes.of(
                AttributeUtils.createStringAttribute("honeycomb.distro.version"), "0.6.1",
                AttributeUtils.createStringAttribute("honeycomb.distro.runtime_version"), System.getProperty("java.runtime.version")
            ),
            resource.getAttributes()
        );
    }
}
