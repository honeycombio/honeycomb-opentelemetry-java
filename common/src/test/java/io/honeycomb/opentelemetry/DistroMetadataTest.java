package io.honeycomb.opentelemetry;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DistroMetadataTest {

    @Test
    public void test_distroMetadata_returns_expected_values() {
        Map<String, String> metadata = DistroMetadata.getMetadata();
        Assertions.assertNotNull(metadata.containsKey(DistroMetadata.VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.VERSION_FIELD), DistroMetadata.VERSION_VALUE);
        Assertions.assertNotNull(metadata.containsKey(DistroMetadata.RUNTIME_VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.RUNTIME_VERSION_FIELD), DistroMetadata.RUNTIME_VERSION_VALUE);
    }
}
