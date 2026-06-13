package com.mealio.domain.model;

public enum MeasurementUnit {
    GRAM("g"),
    SPOON("t"),
    SLICE("w"),
    MILLILITER("ml"),
    PIECE("pcs");

    private final String code;

    MeasurementUnit(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
