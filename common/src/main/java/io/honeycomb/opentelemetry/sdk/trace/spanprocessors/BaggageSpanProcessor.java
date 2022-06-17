package io.honeycomb.opentelemetry.sdk.trace.spanprocessors;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

/**
 * This span processor copies attributes stored in {@link Baggage} into each newly created {@link io.opentelemetry.api.trace.Span}.
 */
public class BaggageSpanProcessor implements SpanProcessor {
    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        Baggage.fromContext(parentContext)
            .forEach((s, baggageEntry) -> span.setAttribute(s, baggageEntry.getValue()));
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan span) {
    }

    @Override
    public boolean isEndRequired() {
        return false;
    }
}

