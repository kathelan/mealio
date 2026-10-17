package com.mealio.asr.infrastructure.asr;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.mealio.asr.domain.exception.AsrProviderConfigurationException;
import com.mealio.asr.domain.exception.AsrProviderException;
import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRequest;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OpenAiAsrProviderAdapterTest {
    private final AsrProperties properties = new AsrProperties();
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    private OpenAiAsrProviderAdapter adapter() {
        properties.getOpenai().setApiKey("test-key");
        properties.getOpenai().setBaseUrl("https://provider.test/v1");
        properties.getOpenai().setModel("test-model");
        return new OpenAiAsrProviderAdapter(properties, builder);
    }

    private TranscriptionRequest request(String filename, String contentType, String prompt) {
        return new TranscriptionRequest(new AudioContent(filename, contentType, new byte[]{1}), "pl", prompt);
    }

    @Test
    void sendsMultipartWithExplicitAndDefaultAudioMetadata() {
        var adapter = adapter();
        assertThat(adapter.providerName()).isEqualTo("openai");
        server.expect(requestTo("https://provider.test/v1/audio/transcriptions"))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test-model")))
                .andRespond(withSuccess("{\"text\":\"transcript\"}", MediaType.APPLICATION_JSON));
        assertThat(adapter.transcribe(request("audio.wav", "audio/wav", "prompt"))).isEqualTo("transcript");
        server.verify();
        server.reset();
        server.expect(anything()).andExpect(content().string(org.hamcrest.Matchers.containsString("audio.wav")))
                .andRespond(withSuccess("{\"text\":\"default\"}", MediaType.APPLICATION_JSON));
        assertThat(adapter.transcribe(request(null, null, null))).isEqualTo("default");
        server.verify();
    }

    @Test
    void mapsProviderHttpNetworkAndEmptyResponseFailures() {
        var adapter = adapter();
        server.expect(anything()).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThatThrownBy(() -> adapter.transcribe(request("a", "audio/wav", null)))
                .isInstanceOfSatisfying(AsrProviderException.class, error -> {
                    assertThat(error.providerName()).isEqualTo("openai");
                    assertThat(error.providerStatusCode()).isEqualTo(429);
                });
        server.verify();
        server.reset();
        server.expect(anything()).andRespond(withException(new IOException("network")));
        assertThatThrownBy(() -> adapter.transcribe(request("a", "audio/wav", null)))
                .isInstanceOf(AsrProviderException.class).hasMessage("ASR provider request failed");
        server.verify();
        for (var response : new String[]{"", "{\"text\":\" \"}"}) {
            server.reset();
            server.expect(anything()).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
            assertThatThrownBy(() -> adapter.transcribe(request("a", "audio/wav", null)))
                    .isInstanceOf(AsrProviderException.class).hasMessageContaining("empty transcript");
            server.verify();
        }
    }

    @Test
    void validatesConfigurationAndContentTypeBeforeHttp() {
        var adapter = adapter();
        assertThatThrownBy(() -> adapter.transcribe(request("a", "invalid/type/value", null)))
                .isInstanceOf(AsrProviderException.class).hasMessageContaining("content type");
        properties.getOpenai().setApiKey("");
        assertThatThrownBy(() -> adapter.transcribe(request("a", null, null)))
                .isInstanceOf(AsrProviderConfigurationException.class);
        server.verify();
    }
}
