package io.honeycomb.opentelemetry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

import org.mockito.Mock;

import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;

@ExtendWith(MockitoExtension.class)
public class BaggageSpanProcessorTest {

    @Test
    public void test_baggageSpanProcessor_adds_attributes_to_spans(@Mock ReadWriteSpan span) {

        try (BaggageSpanProcessor processor = new BaggageSpanProcessor()) {
            AttributeKey<String> attr = InternalAttributeKeyImpl.create("key", AttributeType.STRING);
            Baggage.current()
                .toBuilder()
                .put(attr.getKey(), "value")
                .build()
                .makeCurrent();

            // TestSpan span = new TestSpan();
            processor.onStart(Context.current(), span);

            verify(span).setAttribute("key", "value");
        }
    }
}
