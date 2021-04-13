package com.example.javaagent;

import io.honeycomb.opentelemetry.sdk.trace.samplers.*;
import io.opentelemetry.sdk.autoconfigure.spi.*;
import io.opentelemetry.sdk.trace.*;

public class HoneycombSdkTracerProviderConfigurer implements SdkTracerProviderConfigurer {
    @Override
    public void configure(SdkTracerProviderBuilder tracerProvider) {
        tracerProvider
            .setSampler(new DeterministicTraceSampler(2))
            .addSpanProcessor(new BaggageSpanProcessor());
    }
}
