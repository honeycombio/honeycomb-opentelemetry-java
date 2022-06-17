package io.honeycomb.opentelemetry.sdk.trace.samplers;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.List;

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

    private final Sampler baseSampler;
    private final int sampleRate;

    public final static String DESCRIPTION = "HoneycombDeterministicSampler";

    /**
     * See the class level javadoc for an explanation of the sampleRate.
     *
     * @param sampleRate to use - must not be negative.
     * @throws IllegalArgumentException if sampleRate is negative.
     * @throws IllegalStateException    if SHA-1 is not supported.
     */
    public DeterministicTraceSampler(final int sampleRate) {
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

        SamplingResult result = baseSampler.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
        return SamplingResult.create(
            result.getDecision(),
            Attributes.of(SAMPLE_RATE_ATTRIBUTE_KEY, (long) sampleRate)
        );
    }
}
