package io.honeycomb.opentelemetry.sdk.trace.samplers;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.List;
import java.util.Objects;

/**
 * This TraceSampler allows for distributed sampling based on a common field
 * such as a request or trace ID. It accepts a sample rate N and will
 * deterministically sample 1/N events based on the target field. Hence, two or
 * more processes can decide whether to sample related events without
 * communication.
 * <p>
 * - A sample rate of 0 means the TraceSampler will never sample. <br>
 * - A sampler rate of 1 means it will always samples.
 * <p>
 * This implementation is based on the implementations (and necessarily needs to
 * be in line with) the other Beeline implementations.
 *
 * <h1>Thread-safety</h1> Instances of this class are thread-safe and can be
 * shared.
 *
 * @see <a href=
 *      "https://github.com/honeycombio/beeline-go/blob/main/sample/deterministic_sampler.go">
 *      Go sampler</a>
 * @see <a href=
 *      "https://github.com/honeycombio/beeline-nodejs/blob/main/lib/deterministic_sampler.js">
 *      Nodejs sampler</a>
 */
public class DeterministicTraceSampler implements Sampler {
    private static final int ALWAYS_SAMPLE = 1;
    private static final int NEVER_SAMPLE = 0;

    private static final AttributeKey<Long> SAMPLE_RATE_ATTRIBUTE_KEY = AttributeKey.longKey("SampleRate");

    private final Sampler decoratedSampler;
    private final Sampler baseSampler;
    private final int sampleRate;

    public final static String DESCRIPTION = "HoneycombDeterministicSampler";

    /**
     * Creates a DeterministicTraceSampler which samples a span if the provided decoratedSampler
     * AND this sampler's deterministic sampleRate both decide that a span should be sampled.
     *
     * The decoratedSampler is always called first.
     * When it doesn't decide to DROP the span, the sampleRate determines the final outcome.
     *
     * @param decoratedSampler another Sampler whose decision to sample a span is combined with the
     *                         sample rate.
     *
     * @param sampleRate to use - class level javadoc.
     * @throws IllegalArgumentException if sampleRate is negative.
     * @throws IllegalStateException    if SHA-1 is not supported.
     */
    public DeterministicTraceSampler(final Sampler decoratedSampler, final int sampleRate) {
        Objects.requireNonNull(decoratedSampler, "decoratedSampler cannot be null");
        this.decoratedSampler = decoratedSampler;
        this.sampleRate = sampleRate;
        double ratio;
        if (sampleRate == ALWAYS_SAMPLE) {
            ratio = 1.0;
        } else if (sampleRate == NEVER_SAMPLE) {
            ratio = 0.0;
        } else {
            ratio = 1.0 / sampleRate;
        }
        baseSampler = Sampler.traceIdRatioBased(ratio);
    }

    /**
     * Creates a DeterministicTraceSampler that makes the final decision about whether to sample
     * a Span based on its sampleRate.
     *
     * @param sampleRate to use - class level javadoc.
     * @throws IllegalArgumentException if sampleRate is negative.
     * @throws IllegalStateException    if SHA-1 is not supported.
     */
    public DeterministicTraceSampler(final int sampleRate) {
        this(Sampler.alwaysOn(), sampleRate);
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public SamplingResult shouldSample(
        Context parentContext,
        String traceId,
        String name,
        SpanKind spanKind,
        Attributes attributes,
        List<LinkData> parentLinks) {

        SamplingResult result =
            decoratedSampler.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
        SamplingDecision decision = result.getDecision();
        if (decision != SamplingDecision.DROP) {
            decision = baseSampler.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks)
                .getDecision();
        }
        Attributes newAttributes = Attributes.builder()
            .putAll(result.getAttributes())
            .put(SAMPLE_RATE_ATTRIBUTE_KEY, (long) sampleRate)
            .build();

        return SamplingResult.create(decision, newAttributes);
    }
}
