package io.honeycomb.opentelemetry;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DistroMetadataTest {

    @Test
    public void test_distroMetadata_returns_expected_values() {
        Map<String, String> metadata = DistroMetadata.getMetadata();
        Assertions.assertNotNull(metadata.containsKey("honeycomb.distro.version"));
        Assertions.assertEquals(metadata.get("honeycomb.distro.version"), DistroMetadata.VERSION_VALUE);
        Assertions.assertNotNull(metadata.containsKey("honeycomb.distro.runtime_version"));
        Assertions.assertEquals(metadata.get("honeycomb.distro.runtime_version"), DistroMetadata.RUNTIME_VERSION_VALUE);
    }
}
