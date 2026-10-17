package com.mealio.asr.infrastructure.asr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mealio.asr.domain.exception.AsrProviderConfigurationException;
import com.mealio.asr.domain.exception.AsrProviderException;
import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class OpenAiAsrProviderAdapterIntegrationTest {

    private HttpServer server;
    private CapturedRequest capturedRequest;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsMultipartRequestAndMapsOpenAiTranscriptionResponse() throws IOException {
        startServerWithTranscription("Raz, dwa, trzy.");
        var adapter = new OpenAiAsrProviderAdapter(properties("test-key", baseUrl()), RestClient.builder());
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "audio/wav", new byte[] {1, 2, 3}),
                "pl",
                null
        );

        var transcript = adapter.transcribe(request);

        assertThat(transcript).isEqualTo("Raz, dwa, trzy.");
        assertThat(capturedRequest.path()).isEqualTo("/audio/transcriptions");
        assertThat(capturedRequest.authorization()).isEqualTo("Bearer test-key");
        assertThat(capturedRequest.contentType()).contains("multipart/form-data");
        assertThat(capturedRequest.body()).contains("name=\"model\"");
        assertThat(capturedRequest.body()).contains("gpt-4o-mini-transcribe");
        assertThat(capturedRequest.body()).contains("name=\"language\"");
        assertThat(capturedRequest.body()).contains("pl");
        assertThat(capturedRequest.body()).contains("filename=\"meal.wav\"");
        assertThat(capturedRequest.body()).doesNotContain("name=\"prompt\"");
    }

    @Test
    void rejectsMissingApiKeyBeforeCallingProvider() {
        var adapter = new OpenAiAsrProviderAdapter(properties("", "https://api.openai.com/v1"), RestClient.builder());
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "audio/wav", new byte[] {1}),
                "pl",
                null
        );

        assertThatThrownBy(() -> adapter.transcribe(request))
                .isInstanceOf(AsrProviderConfigurationException.class)
                .hasMessage("mealio.asr.openai.api-key is required for OpenAI ASR provider");
    }

    @Test
    void mapsProviderHttpStatusWithoutParsingProviderBody() throws IOException {
        startErrorServer(400, "{\"error\":{\"message\":\"Unsupported audio format\"}}");
        var adapter = new OpenAiAsrProviderAdapter(properties("test-key", baseUrl()), RestClient.builder());
        var request = new TranscriptionRequest(
                new AudioContent("meal.txt", "text/plain", "not audio".getBytes(StandardCharsets.UTF_8)),
                "pl",
                null
        );

        assertThatThrownBy(() -> adapter.transcribe(request))
                .isInstanceOf(AsrProviderException.class)
                .hasMessage("ASR provider request failed")
                .extracting("providerName", "providerStatusCode")
                .containsExactly("openai", 400);
    }

    @Test
    void rejectsInvalidAudioContentTypeBeforeCallingProvider() throws IOException {
        startServerWithTranscription("not called");
        var adapter = new OpenAiAsrProviderAdapter(properties("test-key", baseUrl()), RestClient.builder());
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "not a media type", new byte[] {1}),
                "pl",
                null
        );

        assertThatThrownBy(() -> adapter.transcribe(request))
                .isInstanceOf(AsrProviderException.class)
                .hasMessage("Uploaded audio content type is invalid")
                .extracting("providerName", "providerStatusCode")
                .containsExactly("openai", null);
    }

    private void startServerWithTranscription(String transcript) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/audio/transcriptions", exchange -> {
            var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.ISO_8859_1);
            capturedRequest = new CapturedRequest(
                    exchange.getRequestURI().getPath(),
                    exchange.getRequestHeaders().getFirst("Authorization"),
                    exchange.getRequestHeaders().getFirst("Content-Type"),
                    body
            );

            var response = """
                    {"text":"%s"}
                    """.formatted(transcript).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
    }

    private void startErrorServer(int status, String responseBody) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/audio/transcriptions", exchange -> {
            var response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
    }

    private String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private AsrProperties properties(String apiKey, String baseUrl) {
        var properties = new AsrProperties();
        properties.setProvider("openai");
        properties.getOpenai().setApiKey(apiKey);
        properties.getOpenai().setBaseUrl(baseUrl);
        properties.getOpenai().setModel("gpt-4o-mini-transcribe");
        return properties;
    }

    private record CapturedRequest(
            String path,
            String authorization,
            String contentType,
            String body
    ) {
    }
}
