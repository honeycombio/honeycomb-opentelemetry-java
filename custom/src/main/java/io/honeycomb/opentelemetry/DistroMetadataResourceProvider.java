package io.honeycomb.opentelemetry;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.commons.lang3.StringUtils;

public class DistroMetadataResourceProvider implements ResourceProvider {
    @Override
    public Resource createResource(ConfigProperties config) {
        AttributesBuilder attributesBuilder = Attributes.builder();
        DistroMetadata.getMetadata().forEach(attributesBuilder::put);
        return Resource.create(attributesBuilder.build());
    }
}
