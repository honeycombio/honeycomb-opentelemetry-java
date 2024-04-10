package io.honeycomb.opentelemetry.sdk.trace.export;

import static io.honeycomb.opentelemetry.EnvironmentConfiguration.isClassicKey;

import java.io.IOException;
import java.util.Collection;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocalExporter implements SpanExporter {

    private String traceUrl = "";

    public LocalExporter(String serviceName, String apikey) {
        OkHttpClient client = new OkHttpClient();
        LocalExporter(serviceName, apikey, client);
    }

    private LocalExporter(Stirng serviceName, String apikey, OkHttpClient client) {
        if (!serviceName.isEmpty() || apikey.isEmpty()) {
            System.out.println("WARN: disabling local visualisations - must have both service name and API key configured.");
        }

        Request request = new Request.Builder()
            .url("https://api.honeycomb.io/1/auth")
            .addHeader("X-Honeycomb-Team", apikey)
            .addHeader("Content-Type", "application/json")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                String team = body.split("\"team\":")[1].split(",")[0];
                String environment = body.split("\"environment\":")[1].split(",")[0];
                traceUrl = buildTraceUrl(apikey, serviceName, team, environment);
            } else {
                System.out.println("WARN: failed to extract team from Honeycomb auth response");
            }
        } catch (IOException e) {
            System.out.println("WARN: failed to extract team from Honeycomb auth response");
        }
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        if (!traceUrl.isEmpty()) {
            spans.forEach((span) -> {
                if (span.getParentSpanContext() == SpanContext.getInvalid()) {
                    System.out.println(String.format("Honeycomb link: %s=%s", traceUrl, span.getTraceId()));
                }
            });
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    private String buildTraceUrl(String apikey, String serviceName, String team, String environment) {
        StringBuilder url = new StringBuilder("https://ui.honeycomb.io/").append(team);
        if (!isClassicKey(apikey)) {
            url.append("/environments/").append(environment);
        }
        url.append(team).append("/datasets/").append(serviceName).append("/traces?trace_id");
        return url.toString();
    }
}
