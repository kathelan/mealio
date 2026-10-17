package com.mealio.asr.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mealio.asr.domain.exception.TranscriptionPersistenceException;
import com.mealio.asr.domain.model.AudioMetadata;
import com.mealio.asr.domain.model.TranscriptionRecord;
import com.mealio.asr.domain.model.TranscriptionStatus;
import com.mealio.asr.infrastructure.persistence.mapper.TranscriptionPersistenceMapper;
import com.mealio.asr.infrastructure.persistence.repository.TranscriptionJpaRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

class JpaTranscriptionRecordRepositoryTest {
    @Test
    void mapsAllMetadataAndOptionalValuesAndWrapsDatabaseFailure() {
        var jpa = mock(TranscriptionJpaRepository.class);
        var mapper = new TranscriptionPersistenceMapper();
        var adapter = new JpaTranscriptionRecordRepository(jpa, mapper);
        when(jpa.save(any())).thenAnswer(call -> call.getArgument(0));
        var record = new TranscriptionRecord(null, "text", "pl", "mock", "model", 0.9,
                new AudioMetadata("a.wav", "audio/wav", 4L), TranscriptionStatus.COMPLETED,
                "code", "message", null, Instant.EPOCH);
        var saved = adapter.create(record);
        assertThat(saved).isEqualTo(record);
        var empty = new TranscriptionRecord(null, "text", "pl", "mock", null, null,
                null, TranscriptionStatus.FAILED, null, null, null, null);
        var mapped = mapper.toDomain(mapper.toEntity(empty));
        assertThat(mapped.confidence()).isNull();
        assertThat(mapped.audio()).isEqualTo(new AudioMetadata(null, null, null));
        when(jpa.save(any())).thenThrow(new DataAccessResourceFailureException("offline"));
        assertThatThrownBy(() -> adapter.create(record)).isInstanceOf(TranscriptionPersistenceException.class)
                .hasMessage("Cannot save transcription record").hasCauseInstanceOf(DataAccessResourceFailureException.class);
    }
}
