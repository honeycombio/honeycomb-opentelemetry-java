package io.honeycomb.opentelemetry;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import io.honeycomb.opentelemetry.sdk.trace.spanprocessors.BaggageSpanProcessor;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.data.SpanData;

public class BaggageSpanProcessorTest {

    @Test
    public void test_baggageSpanProcessor_adds_attributes_to_spans() {
        try (BaggageSpanProcessor processor = new BaggageSpanProcessor()) {
            AttributeKey<String> attr = InternalAttributeKeyImpl.create("key", AttributeType.STRING);
            Baggage.current()
                .toBuilder()
                .put(attr.getKey(), "value")
                .build()
                .makeCurrent();

            TestSpan span = new TestSpan();
            processor.onStart(Context.current(), span);

            Assertions.assertEquals("value", span.attributes.get(attr));
        }
    }

    public final class TestSpan implements ReadWriteSpan {

        Attributes attributes = Attributes.empty();

        @Override
        public <T> Span setAttribute(AttributeKey<T> key, T value) {
            attributes = Attributes.builder().putAll(attributes)
                .put(key, value)
                .build();
            return this;
        }

        @Override
        public <T> T getAttribute(AttributeKey<T> key) {
            return attributes.get(key);
        }

        // unimplemented below

        @Override
        public Span addEvent(String name, Attributes attributes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Span addEvent(String name, Attributes attributes, long timestamp, TimeUnit unit) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Span setStatus(StatusCode statusCode, String description) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Span recordException(Throwable exception, Attributes additionalAttributes) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Span updateName(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void end() {
            // TODO Auto-generated method stub

        }

        @Override
        public void end(long timestamp, TimeUnit unit) {
            // TODO Auto-generated method stub

        }

        @Override
        public SpanContext getSpanContext() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isRecording() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public SpanContext getParentSpanContext() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public SpanData toSpanData() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean hasEnded() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public long getLatencyNanos() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public SpanKind getKind() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
