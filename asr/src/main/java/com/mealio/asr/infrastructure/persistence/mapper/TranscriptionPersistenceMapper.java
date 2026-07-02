package com.mealio.asr.infrastructure.persistence.mapper;

import com.mealio.asr.domain.model.AudioMetadata;
import com.mealio.asr.domain.model.TranscriptionRecord;
import com.mealio.asr.domain.model.TranscriptionStatus;
import com.mealio.asr.infrastructure.persistence.entity.TranscriptionEntity;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class TranscriptionPersistenceMapper {

    public TranscriptionEntity toEntity(TranscriptionRecord record) {
        var entity = new TranscriptionEntity();
        entity.setTranscript(record.transcript());
        entity.setLanguage(record.language());
        entity.setProvider(record.provider());
        entity.setModel(record.model());
        entity.setConfidence(toBigDecimal(record.confidence()));
        entity.setAudioFilename(record.audio() == null ? null : record.audio().filename());
        entity.setAudioContentType(record.audio() == null ? null : record.audio().contentType());
        entity.setAudioSizeBytes(record.audio() == null ? null : record.audio().sizeBytes());
        entity.setStatus(record.status().name());
        entity.setErrorCode(record.errorCode());
        entity.setErrorMessage(record.errorMessage());
        entity.setTranscribedAt(record.transcribedAt());
        return entity;
    }

    public TranscriptionRecord toDomain(TranscriptionEntity entity) {
        return new TranscriptionRecord(
                entity.getId(),
                entity.getTranscript(),
                entity.getLanguage(),
                entity.getProvider(),
                entity.getModel(),
                toDouble(entity.getConfidence()),
                new AudioMetadata(
                        entity.getAudioFilename(),
                        entity.getAudioContentType(),
                        entity.getAudioSizeBytes()
                ),
                TranscriptionStatus.valueOf(entity.getStatus()),
                entity.getErrorCode(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getTranscribedAt()
        );
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    private Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}
