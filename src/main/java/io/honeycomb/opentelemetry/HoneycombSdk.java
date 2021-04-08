package io.honeycomb.opentelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import javax.annotation.concurrent.ThreadSafe;

/**
 * The Honeycomb SDK implementation of {@link OpenTelemetry}.
 *
 * This class exists to make it easier and more intuitive to use
 * Honeycomb with OpenTelemetry.
 */
public final class HoneycombSdk implements OpenTelemetry {

    private final SdkTracerProvider tracerProvider;
    private final ContextPropagators propagators;

    HoneycombSdk(SdkTracerProvider tracerProvider, ContextPropagators propagators) {
        this.tracerProvider = tracerProvider;
        this.propagators = propagators;
    }

    /**
     * Returns a new {@link HoneycombSdkBuilder} for configuring an instance of {@linkplain
     * HoneycombSdk the OpenTelemetry SDK}.
     */
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

    /**
     * This class allows the SDK to unobfuscate an obfuscated static global provider.
     *
     * <p>Static global providers are obfuscated when they are returned from the API to prevent users
     * from casting them to their SDK specific implementation. For example, we do not want users to
     * use patterns like {@code (TracerSdkProvider) OpenTelemetry.getGlobalTracerProvider()}.
     */
    @ThreadSafe
    // Visible for testing
    static class ObfuscatedTracerProvider implements TracerProvider {

        private final SdkTracerProvider delegate;

        ObfuscatedTracerProvider(SdkTracerProvider delegate) {
            this.delegate = delegate;
        }

        @Override
        public Tracer get(String instrumentationName) {
            return delegate.get(instrumentationName);
        }

        @Override
        public Tracer get(String instrumentationName, String instrumentationVersion) {
            return delegate.get(instrumentationName, instrumentationVersion);
        }

        public SdkTracerProvider unobfuscate() {
            return delegate;
        }
    }

}
