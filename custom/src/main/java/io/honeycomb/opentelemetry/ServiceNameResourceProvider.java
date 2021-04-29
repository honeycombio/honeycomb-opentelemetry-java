package io.honeycomb.opentelemetry;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.commons.lang3.StringUtils;

public class ServiceNameResourceProvider implements ResourceProvider {
    private static final AttributeKey<String> SERVICE_NAME_FIELD =
        AttributeKey.stringKey(EnvironmentConfiguration.SERVICE_NAME_FIELD);

    @Override
    public Resource createResource(ConfigProperties config) {
        String serviceName = EnvironmentConfiguration.getServiceName();
        return StringUtils.isNotEmpty(serviceName)
            ? Resource.create(Attributes.of(SERVICE_NAME_FIELD, serviceName))
            : Resource.empty();
    }
}
