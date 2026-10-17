package com.mealio.asr.adapter.in.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.asr.domain.exception.AsrProviderConfigurationException;
import com.mealio.asr.domain.exception.AsrProviderException;
import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import com.mealio.asr.domain.exception.TranscriptionPersistenceException;
import com.mealio.asr.domain.model.TranscriptionResult;
import com.mealio.asr.domain.port.in.TranscribeAudioUseCase;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class TranscriptionControllerTest {
    @Test
    void mapsMultipartAndResultAndRejectsUnreadableUpload() throws Exception {
        var useCase = mock(TranscribeAudioUseCase.class);
        var result = new TranscriptionResult(UUID.randomUUID(), "text", "pl", "mock", null, Instant.EPOCH);
        when(useCase.transcribe(any())).thenReturn(result);
        var controller = new TranscriptionController(useCase);
        var response = controller.transcribe(new MockMultipartFile("file", "test.txt", "text/plain", new byte[]{1}),
                "pl", "hint");
        assertThat(response.id()).isEqualTo(result.id());
        assertThat(response.transcript()).isEqualTo("text");
        var request = org.mockito.ArgumentCaptor.forClass(com.mealio.asr.domain.model.TranscriptionRequest.class);
        verify(useCase).transcribe(request.capture());
        assertThat(request.getValue().audio().bytes()).containsExactly((byte) 1);
        assertThat(request.getValue().prompt()).isEqualTo("hint");
        var file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("read failure"));
        assertThatThrownBy(() -> controller.transcribe(file, "pl", null))
                .isInstanceOf(InvalidTranscriptionRequestException.class).hasCauseInstanceOf(IOException.class);
    }

    @Test
    void errorsHaveStableCodesAndClockTimestamp() {
        var handler = new AsrExceptionHandler(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        assertThat(handler.handleInvalidRequest(new InvalidTranscriptionRequestException("bad")).code())
                .isEqualTo("INVALID_TRANSCRIPTION_REQUEST");
        assertThat(handler.handleProviderConfiguration(new AsrProviderConfigurationException("config")).code())
                .isEqualTo("ASR_PROVIDER_CONFIGURATION_ERROR");
        assertThat(handler.handleProviderFailure(new AsrProviderException("failure")).message()).isEqualTo("failure");
        assertThat(handler.handleProviderFailure(new AsrProviderException("failure", new IOException())).timestamp())
                .isEqualTo(Instant.EPOCH);
        assertThat(handler.handleProviderFailure(new AsrProviderException("failure", "openai", 429, null)).message())
                .isEqualTo("failure (provider=openai, status=429)");
        assertThat(handler.handlePersistenceFailure(new TranscriptionPersistenceException("database", null)).code())
                .isEqualTo("TRANSCRIPTION_PERSISTENCE_ERROR");
    }
}
