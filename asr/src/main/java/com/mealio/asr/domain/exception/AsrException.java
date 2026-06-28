package com.mealio.asr.domain.exception;

public abstract class AsrException extends RuntimeException {

    protected AsrException(String message) {
        super(message);
    }

    protected AsrException(String message, Throwable cause) {
        super(message, cause);
    }
}
