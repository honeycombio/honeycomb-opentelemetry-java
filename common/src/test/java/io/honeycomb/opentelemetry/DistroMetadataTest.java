package io.honeycomb.opentelemetry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class DistroMetadataTest {

    @Test
    public void test_agentMetadata_returns_expected_values() {
        Map<String, String> metadata = DistroMetadata.getAgentMetadata();
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.VERSION_FIELD), DistroMetadata.VERSION_VALUE);
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.RUNTIME_VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.RUNTIME_VERSION_FIELD), DistroMetadata.RUNTIME_VERSION_VALUE);
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.VARIANT_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.VARIANT_FIELD), DistroMetadata.VARIANT_AGENT);
    }

    @Test
    public void test_sdkMetadata_returns_expected_values() {
        Map<String, String> metadata = DistroMetadata.getSDKMetadata();
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.VERSION_FIELD), DistroMetadata.VERSION_VALUE);
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.RUNTIME_VERSION_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.RUNTIME_VERSION_FIELD), DistroMetadata.RUNTIME_VERSION_VALUE);
        Assertions.assertTrue(metadata.containsKey(DistroMetadata.VARIANT_FIELD));
        Assertions.assertEquals(metadata.get(DistroMetadata.VARIANT_FIELD), DistroMetadata.VARIANT_SDK);
    }
}
