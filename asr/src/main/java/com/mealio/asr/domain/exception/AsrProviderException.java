package com.mealio.asr.domain.exception;

public class AsrProviderException extends AsrException {

    public AsrProviderException(String message) {
        super(message);
    }

    public AsrProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
