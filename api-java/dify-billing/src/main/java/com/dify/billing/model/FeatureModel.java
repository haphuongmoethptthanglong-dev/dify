package com.dify.billing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Tenant-scoped feature flags returned by {@code GET /console/api/features}.
 *
 * <p>The {@code is_allow_transfer_workspace} field requires an explicit
 * {@link JsonProperty} annotation — see {@link SystemFeatureModel} for rationale.
 */
public class FeatureModel {

    private BillingModel billing;
    private EducationModel education;
    private LimitationModel members;
    private LimitationModel apps;
    private LimitationModel vectorSpace;
    private int knowledgeRateLimit;
    private LimitationModel annotationQuotaLimit;
    private LimitationModel documentsUploadQuota;
    private String docsProcessing;
    private boolean canReplaceLogo;
    private boolean modelLoadBalancingEnabled;
    private boolean datasetOperatorEnabled;
    private boolean webappCopyrightEnabled;
    private LicenseLimitationModel workspaceMembers;

    @JsonProperty("is_allow_transfer_workspace")
    private boolean allowTransferWorkspace;
    private QuotaModel triggerEvent;
    private QuotaModel apiRateLimit;
    private boolean humanInputEmailDeliveryEnabled;
    private KnowledgePipelineModel knowledgePipeline;
    private int nextCreditResetDate;

    public FeatureModel() {
        this.billing = new BillingModel();
        this.education = new EducationModel();
        this.members = new LimitationModel(0, 1);
        this.apps = new LimitationModel(0, 10);
        this.vectorSpace = new LimitationModel(0, 5);
        this.knowledgeRateLimit = 10;
        this.annotationQuotaLimit = new LimitationModel(0, 10);
        this.documentsUploadQuota = new LimitationModel(0, 50);
        this.docsProcessing = "standard";
        this.canReplaceLogo = false;
        this.modelLoadBalancingEnabled = false;
        this.datasetOperatorEnabled = false;
        this.webappCopyrightEnabled = false;
        this.workspaceMembers = new LicenseLimitationModel(false, 0, 0);
        this.allowTransferWorkspace = true;
        this.triggerEvent = new QuotaModel(0, 3000, 0);
        this.apiRateLimit = new QuotaModel(0, 5000, 0);
        this.humanInputEmailDeliveryEnabled = false;
        this.knowledgePipeline = new KnowledgePipelineModel();
        this.nextCreditResetDate = 0;
    }

    public BillingModel getBilling() {
        return billing;
    }

    public void setBilling(BillingModel billing) {
        this.billing = billing;
    }

    public EducationModel getEducation() {
        return education;
    }

    public void setEducation(EducationModel education) {
        this.education = education;
    }

    public LimitationModel getMembers() {
        return members;
    }

    public void setMembers(LimitationModel members) {
        this.members = members;
    }

    public LimitationModel getApps() {
        return apps;
    }

    public void setApps(LimitationModel apps) {
        this.apps = apps;
    }

    public LimitationModel getVectorSpace() {
        return vectorSpace;
    }

    public void setVectorSpace(LimitationModel vectorSpace) {
        this.vectorSpace = vectorSpace;
    }

    public int getKnowledgeRateLimit() {
        return knowledgeRateLimit;
    }

    public void setKnowledgeRateLimit(int knowledgeRateLimit) {
        this.knowledgeRateLimit = knowledgeRateLimit;
    }

    public LimitationModel getAnnotationQuotaLimit() {
        return annotationQuotaLimit;
    }

    public void setAnnotationQuotaLimit(LimitationModel annotationQuotaLimit) {
        this.annotationQuotaLimit = annotationQuotaLimit;
    }

    public LimitationModel getDocumentsUploadQuota() {
        return documentsUploadQuota;
    }

    public void setDocumentsUploadQuota(LimitationModel documentsUploadQuota) {
        this.documentsUploadQuota = documentsUploadQuota;
    }

    public String getDocsProcessing() {
        return docsProcessing;
    }

    public void setDocsProcessing(String docsProcessing) {
        this.docsProcessing = docsProcessing;
    }

    public boolean isCanReplaceLogo() {
        return canReplaceLogo;
    }

    public void setCanReplaceLogo(boolean canReplaceLogo) {
        this.canReplaceLogo = canReplaceLogo;
    }

    public boolean isModelLoadBalancingEnabled() {
        return modelLoadBalancingEnabled;
    }

    public void setModelLoadBalancingEnabled(boolean modelLoadBalancingEnabled) {
        this.modelLoadBalancingEnabled = modelLoadBalancingEnabled;
    }

    public boolean isDatasetOperatorEnabled() {
        return datasetOperatorEnabled;
    }

    public void setDatasetOperatorEnabled(boolean datasetOperatorEnabled) {
        this.datasetOperatorEnabled = datasetOperatorEnabled;
    }

    public boolean isWebappCopyrightEnabled() {
        return webappCopyrightEnabled;
    }

    public void setWebappCopyrightEnabled(boolean webappCopyrightEnabled) {
        this.webappCopyrightEnabled = webappCopyrightEnabled;
    }

    public LicenseLimitationModel getWorkspaceMembers() {
        return workspaceMembers;
    }

    public void setWorkspaceMembers(LicenseLimitationModel workspaceMembers) {
        this.workspaceMembers = workspaceMembers;
    }

    public boolean isAllowTransferWorkspace() {
        return allowTransferWorkspace;
    }

    public void setAllowTransferWorkspace(boolean allowTransferWorkspace) {
        this.allowTransferWorkspace = allowTransferWorkspace;
    }

    public QuotaModel getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(QuotaModel triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public QuotaModel getApiRateLimit() {
        return apiRateLimit;
    }

    public void setApiRateLimit(QuotaModel apiRateLimit) {
        this.apiRateLimit = apiRateLimit;
    }

    public boolean isHumanInputEmailDeliveryEnabled() {
        return humanInputEmailDeliveryEnabled;
    }

    public void setHumanInputEmailDeliveryEnabled(boolean humanInputEmailDeliveryEnabled) {
        this.humanInputEmailDeliveryEnabled = humanInputEmailDeliveryEnabled;
    }

    public KnowledgePipelineModel getKnowledgePipeline() {
        return knowledgePipeline;
    }

    public void setKnowledgePipeline(KnowledgePipelineModel knowledgePipeline) {
        this.knowledgePipeline = knowledgePipeline;
    }

    public int getNextCreditResetDate() {
        return nextCreditResetDate;
    }

    public void setNextCreditResetDate(int nextCreditResetDate) {
        this.nextCreditResetDate = nextCreditResetDate;
    }
}
