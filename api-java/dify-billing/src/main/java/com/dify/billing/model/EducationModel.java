package com.dify.billing.model;

public record EducationModel(
        boolean enabled,
        boolean activated
) {
    public EducationModel() {
        this(false, false);
    }
}
