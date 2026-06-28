package com.mealio.asr.infrastructure.asr;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRequest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MockAsrProviderAdapterTest {

    private final MockAsrProviderAdapter adapter = new MockAsrProviderAdapter();

    @Test
    void returnsTextFileContentAsTranscript() {
        var request = new TranscriptionRequest(
                new AudioContent(
                        "meal.txt",
                        "text/plain",
                        "zjadlem trzy jajka".getBytes(StandardCharsets.UTF_8)
                ),
                "pl",
                null
        );

        assertThat(adapter.transcribe(request)).isEqualTo("zjadlem trzy jajka");
    }

    @Test
    void returnsFilenameBasedTranscriptForBinaryAudio() {
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "audio/wav", new byte[] {1, 2, 3}),
                "pl",
                null
        );

        assertThat(adapter.transcribe(request)).isEqualTo("Mock transcript for meal.wav");
    }
}
