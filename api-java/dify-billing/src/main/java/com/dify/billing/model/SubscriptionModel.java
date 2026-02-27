package com.dify.billing.model;

public record SubscriptionModel(
        String plan,
        String interval
) {
    public SubscriptionModel() {
        this("sandbox", "");
    }
}
