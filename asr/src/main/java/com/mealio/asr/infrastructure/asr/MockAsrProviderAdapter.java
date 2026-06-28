package com.mealio.asr.infrastructure.asr;

import com.mealio.asr.domain.model.TranscriptionRequest;
import com.mealio.asr.domain.port.out.AsrProviderPort;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(prefix = "mealio.asr", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockAsrProviderAdapter implements AsrProviderPort {

    @Override
    public String providerName() {
        return "mock";
    }

    @Override
    public String transcribe(TranscriptionRequest request) {
        if (request.audio().contentType() != null && request.audio().contentType().startsWith("text/")) {
            return new String(request.audio().bytes(), StandardCharsets.UTF_8).trim();
        }

        var filename = StringUtils.hasText(request.audio().filename()) ? request.audio().filename() : "audio";
        return "Mock transcript for " + filename;
    }
}
