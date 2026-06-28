package com.mealio.asr.domain.exception;

public class InvalidTranscriptionRequestException extends AsrException {

    public InvalidTranscriptionRequestException(String message) {
        super(message);
    }

    public InvalidTranscriptionRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
