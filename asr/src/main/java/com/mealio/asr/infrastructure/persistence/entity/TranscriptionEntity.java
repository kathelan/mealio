package com.mealio.asr.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transcriptions")
@NoArgsConstructor
public class TranscriptionEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String transcript;

    private String language;

    @Column(nullable = false)
    private String provider;

    private String model;

    @Column(precision = 5, scale = 4)
    private BigDecimal confidence;

    private String audioFilename;

    private String audioContentType;

    private Long audioSizeBytes;

    @Column(nullable = false)
    private String status;

    private String errorCode;

    @Column(columnDefinition = "text")
    private String errorMessage;

    private Instant transcribedAt;
}
