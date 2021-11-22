package io.honeycomb.opentelemetry;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;

public class AttributeUtils {

    final static AttributeKey<String> createStringAttribute(String key) {
        return InternalAttributeKeyImpl.create(key, AttributeType.STRING);
    }
}
