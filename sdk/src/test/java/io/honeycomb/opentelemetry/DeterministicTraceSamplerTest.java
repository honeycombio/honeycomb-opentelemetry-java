package io.honeycomb.opentelemetry;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import io.honeycomb.opentelemetry.sdk.trace.samplers.DeterministicTraceSampler;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;

public class DeterministicTraceSamplerTest {

    @Test
    public void test_neverSample() {
        int sampleRate = 0;
        Sampler sampler = new DeterministicTraceSampler(sampleRate);

        for (int i = 0; i < 100; i++) {
            String traceID = IdGenerator.random().generateTraceId();
            SamplingResult result = sampler.shouldSample(Context.current(), traceID, "span", SpanKind.CLIENT, Attributes.empty(), Collections.emptyList());
            Assertions.assertEquals(SamplingDecision.DROP, result.getDecision());
        }
    }

    @Test
    public void test_alwaysSample() {
        int sampleRate = 1;
        Sampler sampler = new DeterministicTraceSampler(sampleRate);

        for (int i = 0; i < 100; i++) {
            String traceID = IdGenerator.random().generateTraceId();
            SamplingResult result = sampler.shouldSample(Context.current(), traceID, "span", SpanKind.CLIENT, Attributes.empty(), Collections.emptyList());
            Assertions.assertEquals(SamplingDecision.RECORD_AND_SAMPLE, result.getDecision());
        }
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    public void test_(String traceID, SamplingDecision expecteDecision) {
        Sampler sampler = new DeterministicTraceSampler(2);
        SamplingResult result = sampler.shouldSample(Context.current(), traceID, "span", SpanKind.CLIENT, Attributes.empty(), Collections.emptyList());
        Assertions.assertEquals(expecteDecision, result.getDecision());
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
            Arguments.of("a5a013ab53993340b648bc38ab92318d", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("f30eb8bab58b954ebc2b99a27ad23ba5", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("f380ceeefeb9914f831b0294c59d454e", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("0875071575a9ce47b52732b4ca64ccc9", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("4d7feb838b90fb4c88a4905b39460cfa", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("741de0d981866e47b2207fcb9be1c207", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("eda648054e388d49a80cffaef8d182bc", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("ed4089963f24634dbc91cc2b3d55407c", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("e06e17e6b57fc54489e0e205f060a1e5", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("d58153786958be408106f7297836229f", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("5a8794bd6402bd429054fa71fb58b68c", SamplingDecision.RECORD_AND_SAMPLE),
            Arguments.of("7f09606a8908fc49a824cf2189a7e087", SamplingDecision.DROP),
            Arguments.of("25c9c9f1002576469999e69e02c0be7e", SamplingDecision.DROP),
            Arguments.of("c7f818f60d780145b4355ab0603d4d0c", SamplingDecision.DROP),
            Arguments.of("817a087bbbed1b4c96258f07d59fe006", SamplingDecision.DROP),
            Arguments.of("46cd1af9a07eaa40b00f227dea73c3fc", SamplingDecision.DROP),
            Arguments.of("384c70f32674c04ca2709c58fe066721", SamplingDecision.DROP),
            Arguments.of("209e3ba9ecc648458b722ee622106a05", SamplingDecision.DROP),
            Arguments.of("560b7b48bfd99c429bfb0d497ea260c6", SamplingDecision.DROP),
            Arguments.of("2f3467b35730064597b4d93440cdc033", SamplingDecision.DROP)
        );
    }
}
