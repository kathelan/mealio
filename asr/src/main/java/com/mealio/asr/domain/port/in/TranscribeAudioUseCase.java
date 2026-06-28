package com.mealio.asr.domain.port.in;

import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.model.TranscriptionResult;

public interface TranscribeAudioUseCase {

    TranscriptionResult transcribe(TranscriptionRequest request);
}
