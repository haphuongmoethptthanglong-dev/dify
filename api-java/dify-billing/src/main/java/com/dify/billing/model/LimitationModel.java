package com.dify.billing.model;

public record LimitationModel(
        int size,
        int limit
) {
    public LimitationModel() {
        this(0, 0);
    }
}
