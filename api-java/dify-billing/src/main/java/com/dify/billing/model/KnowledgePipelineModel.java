package com.dify.billing.model;

public class KnowledgePipelineModel {

    private boolean publishEnabled;

    public KnowledgePipelineModel() {
        this.publishEnabled = false;
    }

    public boolean isPublishEnabled() {
        return publishEnabled;
    }

    public void setPublishEnabled(boolean publishEnabled) {
        this.publishEnabled = publishEnabled;
    }
}
