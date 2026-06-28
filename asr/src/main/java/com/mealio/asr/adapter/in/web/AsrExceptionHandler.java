package com.mealio.asr.adapter.in.web;

import com.mealio.asr.adapter.in.web.dto.ApiErrorResponse;
import com.mealio.asr.domain.exception.AsrProviderConfigurationException;
import com.mealio.asr.domain.exception.AsrProviderException;
import com.mealio.asr.domain.exception.InvalidTranscriptionRequestException;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class AsrExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(InvalidTranscriptionRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiErrorResponse handleInvalidRequest(InvalidTranscriptionRequestException exception) {
        return error("INVALID_TRANSCRIPTION_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(AsrProviderConfigurationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiErrorResponse handleProviderConfiguration(AsrProviderConfigurationException exception) {
        return error("ASR_PROVIDER_CONFIGURATION_ERROR", exception.getMessage());
    }

    @ExceptionHandler(AsrProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    ApiErrorResponse handleProviderFailure(AsrProviderException exception) {
        if (exception.providerStatusCode() == null) {
            return error("ASR_PROVIDER_ERROR", exception.getMessage());
        }
        return error(
                "ASR_PROVIDER_ERROR",
                "%s (provider=%s, status=%d)"
                        .formatted(exception.getMessage(), exception.providerName(), exception.providerStatusCode())
        );
    }

    private ApiErrorResponse error(String code, String message) {
        return new ApiErrorResponse(code, message, Instant.now(clock));
    }
}
