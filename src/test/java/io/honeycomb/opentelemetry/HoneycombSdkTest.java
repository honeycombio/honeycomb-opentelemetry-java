package io.honeycomb.opentelemetry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class HoneycombSdkTest {
    @Mock
    private SdkTracerProvider tracerProvider;
    @Mock
    private ContextPropagators propagators;

    @AfterEach
    void tearDown() {
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    void testRegisterGlobal() {
        HoneycombSdk sdk =
            HoneycombSdk.builder().setPropagators(propagators).buildAndRegisterGlobal();
        assertThat(GlobalOpenTelemetry.get()).extracting("delegate").isSameAs(sdk);
        assertThat(sdk.getTracerProvider().get(""))
            .isSameAs(GlobalOpenTelemetry.getTracerProvider().get(""))
            .isSameAs(GlobalOpenTelemetry.get().getTracer(""));

        assertThat(GlobalOpenTelemetry.getPropagators())
            .isSameAs(GlobalOpenTelemetry.get().getPropagators())
            .isSameAs(sdk.getPropagators())
            .isSameAs(propagators);
    }

    @Test
    void castingGlobalToSdkFails() {
        HoneycombSdk.builder().buildAndRegisterGlobal();

        assertThatThrownBy(
            () -> {
                @SuppressWarnings("unused")
                HoneycombSdk shouldFail = (HoneycombSdk) GlobalOpenTelemetry.get();
            })
            .isInstanceOf(ClassCastException.class);
    }

    @Test
    void testShortcutVersions() {
        assertThat(GlobalOpenTelemetry.getTracer("testTracer1"))
            .isEqualTo(GlobalOpenTelemetry.getTracerProvider().get("testTracer1"));
        assertThat(GlobalOpenTelemetry.getTracer("testTracer2", "testVersion"))
            .isEqualTo(GlobalOpenTelemetry.getTracerProvider().get("testTracer2", "testVersion"));
    }

}
