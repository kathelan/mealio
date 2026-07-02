package com.mealio.asr.infrastructure.persistence.repository;

import com.mealio.asr.infrastructure.persistence.entity.TranscriptionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionJpaRepository extends JpaRepository<TranscriptionEntity, UUID> {
}
