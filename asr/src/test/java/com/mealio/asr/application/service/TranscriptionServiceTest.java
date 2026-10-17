package com.mealio.asr.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import com.mealio.asr.domain.model.AudioMetadata;
import com.mealio.asr.domain.model.AudioContent;
import com.mealio.asr.domain.model.TranscriptionRecord;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.model.TranscriptionStatus;
import com.mealio.asr.domain.port.out.AsrProviderPort;
import com.mealio.asr.domain.port.out.TranscriptionRecordRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TranscriptionServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-06-28T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void returnsTranscriptionResultFromConfiguredProvider() {
        var repository = new InMemoryTranscriptionRecordRepository();
        var service = new TranscriptionService(new StubAsrProviderPort(), repository, clock);
        var request = new TranscriptionRequest(
                new AudioContent("meal.wav", "audio/wav", new byte[] {1, 2, 3}),
                "pl",
                "food logging"
        );

        var result = service.transcribe(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.transcript()).isEqualTo("zjadlem trzy jajka");
        assertThat(result.language()).isEqualTo("pl");
        assertThat(result.provider()).isEqualTo("stub");
        assertThat(result.transcribedAt()).isEqualTo(Instant.parse("2026-06-28T12:00:00Z"));

        assertThat(repository.records).hasSize(1);
        var savedRecord = repository.records.getFirst();
        assertThat(savedRecord.id()).isEqualTo(result.id());
        assertThat(savedRecord.transcript()).isEqualTo("zjadlem trzy jajka");
        assertThat(savedRecord.status()).isEqualTo(TranscriptionStatus.COMPLETED);
        assertThat(savedRecord.audio()).isEqualTo(new AudioMetadata("meal.wav", "audio/wav", 3L));
    }

    @Test
    void rejectsEmptyAudio() {
        var service = new TranscriptionService(new StubAsrProviderPort(), new InMemoryTranscriptionRecordRepository(), clock);
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

    private static class InMemoryTranscriptionRecordRepository implements TranscriptionRecordRepository {

        private final List<TranscriptionRecord> records = new ArrayList<>();

        @Override
        public TranscriptionRecord create(TranscriptionRecord record) {
            var saved = new TranscriptionRecord(
                    UUID.randomUUID(),
                    record.transcript(),
                    record.language(),
                    record.provider(),
                    record.model(),
                    record.confidence(),
                    record.audio(),
                    record.status(),
                    record.errorCode(),
                    record.errorMessage(),
                    Instant.parse("2026-06-28T12:00:01Z"),
                    record.transcribedAt()
            );
            records.add(saved);
            return saved;
        }
    }
    @Test
    void rejectsMissingAudioAndLanguageBeforeCallingProvider() {
        var provider = org.mockito.Mockito.mock(com.mealio.asr.domain.port.out.AsrProviderPort.class);
        var records = org.mockito.Mockito.mock(com.mealio.asr.domain.port.out.TranscriptionRecordRepository.class);
        var subject = new TranscriptionService(provider, records, java.time.Clock.systemUTC());
        var valid = new com.mealio.asr.domain.model.AudioContent("a", "audio/wav", new byte[]{1});
        for (var request : java.util.Arrays.asList(null,
                new com.mealio.asr.domain.model.TranscriptionRequest(null, "pl", null),
                new com.mealio.asr.domain.model.TranscriptionRequest(
                        new com.mealio.asr.domain.model.AudioContent("a", null, null), "pl", null),
                new com.mealio.asr.domain.model.TranscriptionRequest(
                        new com.mealio.asr.domain.model.AudioContent("a", null, new byte[0]), "pl", null),
                new com.mealio.asr.domain.model.TranscriptionRequest(valid, " ", null))) {
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> subject.transcribe(request))
                    .isInstanceOf(com.mealio.asr.domain.exception.InvalidTranscriptionRequestException.class);
        }
        org.mockito.Mockito.verifyNoInteractions(provider, records);
    }
}
