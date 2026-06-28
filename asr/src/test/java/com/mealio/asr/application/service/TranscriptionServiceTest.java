package com.mealio.asr.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.port.out.AsrProviderPort;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class TranscriptionServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-28T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void returnsTranscriptionResultFromConfiguredProvider() {
        var service = new TranscriptionService(new StubAsrProviderPort(), clock);
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "audio/wav", new byte[] {1, 2, 3}),
                "pl",
                "food logging"
        );

        var result = service.transcribe(request);

        assertThat(result.transcript()).isEqualTo("zjadlem trzy jajka");
        assertThat(result.language()).isEqualTo("pl");
        assertThat(result.provider()).isEqualTo("stub");
        assertThat(result.transcribedAt()).isEqualTo(Instant.parse("2026-06-28T12:00:00Z"));
    }

    @Test
    void rejectsEmptyAudio() {
        var service = new TranscriptionService(new StubAsrProviderPort(), clock);
        var request = new TranscriptionRequest(
                new AudioContent("empty.wav", "audio/wav", new byte[] {}),
                "pl",
                null
        );

        assertThatThrownBy(() -> service.transcribe(request))
                .isInstanceOf(InvalidTranscriptionRequestException.class)
                .hasMessage("Audio file is required");
    }

    private static class StubAsrProviderPort implements AsrProviderPort {

        @Override
        public String providerName() {
            return "stub";
        }

        @Override
        public String transcribe(TranscriptionRequest request) {
            return "zjadlem trzy jajka";
        }
    }
}
