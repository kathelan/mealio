package com.mealio.asr.domain.exception;

public class AsrProviderException extends AsrException {

    private final String providerName;
    private final Integer providerStatusCode;

    public AsrProviderException(String message) {
        this(message, null, null, null);
    }

    public AsrProviderException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public AsrProviderException(String message, String providerName, Integer providerStatusCode, Throwable cause) {
        super(message, cause);
        this.providerName = providerName;
        this.providerStatusCode = providerStatusCode;
    }

    public String providerName() {
        return providerName;
    }

    public Integer providerStatusCode() {
        return providerStatusCode;
    }
}
