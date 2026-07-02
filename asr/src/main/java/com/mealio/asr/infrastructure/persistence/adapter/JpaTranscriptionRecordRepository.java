package com.mealio.asr.infrastructure.persistence.adapter;

import com.mealio.asr.domain.exception.TranscriptionPersistenceException;
import com.mealio.asr.domain.model.TranscriptionRecord;
import com.mealio.asr.domain.port.out.TranscriptionRecordRepository;
import com.mealio.asr.infrastructure.persistence.mapper.TranscriptionPersistenceMapper;
import com.mealio.asr.infrastructure.persistence.repository.TranscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTranscriptionRecordRepository implements TranscriptionRecordRepository {

    private final TranscriptionJpaRepository jpaRepository;
    private final TranscriptionPersistenceMapper mapper;

    @Override
    public TranscriptionRecord create(TranscriptionRecord record) {
        try {
            return mapper.toDomain(jpaRepository.save(mapper.toEntity(record)));
        } catch (DataAccessException exception) {
            throw new TranscriptionPersistenceException("Cannot save transcription record", exception);
        }
    }
}
