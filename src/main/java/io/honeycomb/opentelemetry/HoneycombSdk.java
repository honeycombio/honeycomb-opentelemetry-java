package io.honeycomb.opentelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.trace.SdkTracerProvider;

public final class HoneycombSdk implements OpenTelemetry {

    private final SdkTracerProvider tracerProvider;
    private final ContextPropagators propagators;

    HoneycombSdk(SdkTracerProvider tracerProvider, ContextPropagators propagators) {
        this.tracerProvider = tracerProvider;
        this.propagators = propagators;
    }

    public static HoneycombSdkBuilder builder() {
        return new HoneycombSdkBuilder();
    }

    @Override
    public TracerProvider getTracerProvider() {
        return this.tracerProvider;
    }

    @Override
    public ContextPropagators getPropagators() {
        return this.propagators;
    }
}
