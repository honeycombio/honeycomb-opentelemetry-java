package io.honeycomb.javaagent.smoketest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoIdeaDog extends SmokeTest {
    protected static OkHttpClient client = OkHttpUtils.client();

    @Test
    public void springBootSmokeTestOnJDK() throws IOException {
        String url = String.format("http://localhost:5678/otlp-requests");
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();

        System.out.println(response.headers().toString());

        Assertions.assertEquals("application/json", response.headers("Content-Type"));
    }
}
