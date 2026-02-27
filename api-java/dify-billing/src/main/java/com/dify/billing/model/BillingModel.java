package com.dify.billing.model;

public record BillingModel(
        boolean enabled,
        SubscriptionModel subscription
) {
    public BillingModel() {
        this(false, new SubscriptionModel());
    }
}
