package com.mealio.asr.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mealio.asr.domain.model.AudioMetadata;
import com.mealio.asr.domain.model.TranscriptionRecord;
import com.mealio.asr.domain.model.TranscriptionStatus;
import com.mealio.asr.domain.port.out.TranscriptionRecordRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "mealio.asr.provider=mock",
        "spring.liquibase.default-schema=asr",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.default_schema=asr"
})
class PostgreSqlIntegrationTest {
    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine")
            .withInitScript("db/postgres-test-init.sql");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired TranscriptionRecordRepository records;
    @Autowired JdbcTemplate jdbc;

    @Test
    void migratesEmptyDatabaseAndPersistsTranscriptionMetadata() {
        var instant = Instant.parse("2026-10-17T10:00:00Z");
        var record = records.create(new TranscriptionRecord(null, "test transcript", "pl", "test",
                null, 0.9876, new AudioMetadata("test.wav", "audio/wav", 3L),
                TranscriptionStatus.COMPLETED, null, null, null, instant));

        assertThat(record.id()).isNotNull();
        assertThat(record.createdAt()).isNotNull();
        assertThat(record.transcribedAt()).isEqualTo(instant);
        assertThat(record.confidence()).isEqualTo(0.9876);
        assertThat(jdbc.queryForObject("SELECT transcript FROM asr.transcriptions WHERE id = ?",
                String.class, record.id())).isEqualTo("test transcript");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM asr.databasechangelog", Integer.class)).isEqualTo(1);
    }
}
