package com.mealio.asr.domain.port.out;

import com.mealio.asr.domain.model.TranscriptionRecord;

public interface TranscriptionRecordRepository {

    TranscriptionRecord create(TranscriptionRecord record);
}
