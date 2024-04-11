package io.honeycomb.opentelemetry.sdk.trace.export;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;

import okhttp3.*;
import okhttp3.mock.*;

@ExtendWith(MockitoExtension.class)
public class LocalExportTest {

    @Test
    public void testLocalExport(@Mock SpanData span) {
        // create the mock interceptor that will return the team and environment from the auth endpoint
        MockInterceptor interceptor = new MockInterceptor(Behavior.UNORDERED);
        interceptor.addRule()
            .get("https://api.honeycomb.io/1/auth")
            .respond("{\"team\":\"team\",\"environment\":\"env\"}");

        // create the client using the mock interceptor
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

        // mock the span to return an invalid parent context and a span id
        Mockito.when(span.getParentSpanContext()).thenReturn(SpanContext.getInvalid());
        Mockito.when(span.getTraceId()).thenReturn("my-span-id");

        // set up the output stream to capture the output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // create the exporter
        LocalExporter exporter = new LocalExporter("service", "apikey", client);

        // export the span and check the result
        CompletableResultCode result = exporter.export(Arrays.asList(span));
        assertTrue(result.isSuccess());

        // check the url was logged to the console
        String output = out.toString();
        assertTrue(output.contains("https://ui.honeycomb.io/trace-view/team/env/service?trace_id=" + span.getTraceId()));
    }
}
