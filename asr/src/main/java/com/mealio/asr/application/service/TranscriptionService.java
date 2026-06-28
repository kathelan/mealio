package com.mealio.asr.application.service;

import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.model.TranscriptionResult;
import com.mealio.asr.domain.port.in.TranscribeAudioUseCase;
import com.mealio.asr.domain.port.out.AsrProviderPort;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TranscriptionService implements TranscribeAudioUseCase {

    private final AsrProviderPort asrProviderPort;
    private final Clock clock;

    @Override
    public TranscriptionResult transcribe(TranscriptionRequest request) {
        validate(request);

        var transcript = asrProviderPort.transcribe(request);
        return new TranscriptionResult(
                transcript,
                request.language(),
                asrProviderPort.providerName(),
                null,
                Instant.now(clock)
        );
    }

    private void validate(TranscriptionRequest request) {
        if (request == null || request.audio() == null || request.audio().bytes() == null
                || request.audio().bytes().length == 0) {
            throw new InvalidTranscriptionRequestException("Audio file is required");
        }
        if (!StringUtils.hasText(request.language())) {
            throw new InvalidTranscriptionRequestException("Language is required");
        }
    }
}
