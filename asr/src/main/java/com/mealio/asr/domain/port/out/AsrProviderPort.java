package com.mealio.asr.domain.port.out;

import com.mealio.asr.domain.model.TranscriptionRequest;

public interface AsrProviderPort {

    String providerName();

    String transcribe(TranscriptionRequest request);
}
