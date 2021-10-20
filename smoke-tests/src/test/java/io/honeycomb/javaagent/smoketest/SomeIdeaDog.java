package io.honeycomb.javaagent.smoketest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SomeIdeaDog extends SmokeTest {
    protected static OkHttpClient client = OkHttpUtils.client();

    @Test
    public void smokeTestTheSmokeTest() throws IOException {
        startAgentOnlyApp();
        String url = String.format("http://localhost:%d/", agentOnlyApp.getMappedPort(5002));
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();

        String body = response.body().string();

        Assertions.assertEquals("Important Information: Greetings from Spring Boot!", body);
        stopAgentOnlyApp();
    }
}
