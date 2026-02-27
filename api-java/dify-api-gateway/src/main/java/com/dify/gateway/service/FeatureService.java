package com.dify.gateway.service;

import com.dify.billing.model.FeatureModel;
import com.dify.billing.model.SystemFeatureModel;
import com.dify.gateway.config.DifyProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Resolves tenant and system feature flags from configuration, billing, and enterprise sources.
 *
 * <p>Mirrors Python's {@code FeatureService} in {@code api/services/feature_service.py}.
 *
 * <p>For the SELF_HOSTED edition (no billing, no enterprise), features are derived solely from
 * environment/configuration properties.  The structure preserves hooks for billing and enterprise
 * enrichment to be added in later migration slices.
 */
@Service
public class FeatureService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

    private final DifyProperties difyProperties;

    public FeatureService(DifyProperties difyProperties) {
        this.difyProperties = difyProperties;
    }

    /**
     * Build tenant-scoped feature flags.
     *
     * <p>Python equivalent: {@code FeatureService.get_features(tenant_id)}.
     *
     * @param tenantId the current tenant identifier (may be null for unauthenticated requests)
     * @return populated feature model
     */
    public FeatureModel getFeatures(String tenantId) {
        FeatureModel features = new FeatureModel();

        fulfillParamsFromEnv(features);

        if (difyProperties.getBilling().isEnabled() && tenantId != null && !tenantId.isBlank()) {
            // TODO: Slice N — call billing API to enrich features
            fulfillParamsFromBillingApi(features, tenantId);
        }

        if (difyProperties.getEnterprise().isEnabled()) {
            // TODO: Slice N — enterprise enrichment
            features.setWebappCopyrightEnabled(true);
            features.getKnowledgePipeline().setPublishEnabled(true);
            fulfillParamsFromWorkspaceInfo(features, tenantId);
        }

        features.setHumanInputEmailDeliveryEnabled(
                resolveHumanInputEmailDeliveryEnabled(features, tenantId));

        return features;
    }

    /**
     * Build system-wide feature flags (not tenant-scoped).
     *
     * <p>Python equivalent: {@code FeatureService.get_system_features(is_authenticated)}.
     *
     * @param isAuthenticated whether the caller has a valid session
     * @return populated system feature model
     */
    public SystemFeatureModel getSystemFeatures(boolean isAuthenticated) {
        SystemFeatureModel systemFeatures = new SystemFeatureModel();

        fulfillSystemParamsFromEnv(systemFeatures);

        if (difyProperties.getEnterprise().isEnabled()) {
            // TODO: Slice N — enterprise enrichment
            systemFeatures.getBranding().setEnabled(true);
            systemFeatures.getWebappAuth().setEnabled(true);
            systemFeatures.setEnableChangeEmail(false);
            systemFeatures.getPluginManager().setEnabled(true);
            fulfillParamsFromEnterprise(systemFeatures, isAuthenticated);
        }

        if (difyProperties.getFeatures().isEnableMarketplace()) {
            systemFeatures.setEnableMarketplace(true);
        }

        return systemFeatures;
    }

    // ---- environment-based enrichment -------------------------------------------

    /**
     * Populate feature flags from environment/config properties.
     * Mirrors Python's {@code _fulfill_params_from_env}.
     */
    private void fulfillParamsFromEnv(FeatureModel features) {
        DifyProperties.Features cfg = difyProperties.getFeatures();
        features.setCanReplaceLogo(cfg.isCanReplaceLogo());
        features.setModelLoadBalancingEnabled(cfg.isModelLoadBalancingEnabled());
        features.setDatasetOperatorEnabled(cfg.isDatasetOperatorEnabled());
        // EducationModel is a record — replace the instance to change fields
        features.setEducation(new com.dify.billing.model.EducationModel(cfg.isEducationEnabled(), false));
    }

    /**
     * Populate system feature flags from environment/config properties.
     * Mirrors Python's {@code _fulfill_system_params_from_env}.
     */
    private void fulfillSystemParamsFromEnv(SystemFeatureModel systemFeatures) {
        DifyProperties.Login loginCfg = difyProperties.getLogin();
        systemFeatures.setEnableEmailCodeLogin(loginCfg.isEnableEmailCodeLogin());
        systemFeatures.setEnableEmailPasswordLogin(loginCfg.isEnableEmailPasswordLogin());
        systemFeatures.setEnableSocialOauthLogin(loginCfg.isEnableSocialOauthLogin());
        systemFeatures.setAllowRegister(loginCfg.isAllowRegister());
        systemFeatures.setAllowCreateWorkspace(loginCfg.isAllowCreateWorkspace());

        String mailType = difyProperties.getMail().getType();
        systemFeatures.setEmailSetup(mailType != null && !mailType.isEmpty());

        systemFeatures.setTrialModels(deriveTrialModels());

        DifyProperties.Features featuresCfg = difyProperties.getFeatures();
        systemFeatures.setEnableTrialApp(featuresCfg.isEnableTrialApp());
        systemFeatures.setEnableExploreBanner(featuresCfg.isEnableExploreBanner());

        systemFeatures.setMaxPluginPackageSize(difyProperties.getPlugin().getMaxPackageSize());
    }

    // ---- trial model derivation ------------------------------------------------

    /**
     * Derive the list of hosted trial-model provider identifiers.
     *
     * <p>Mirrors Python's {@code _fulfill_trial_models_from_env}: for each hosted provider,
     * include its value string if both {@code paidEnabled} and {@code trialEnabled} are true.
     */
    private List<String> deriveTrialModels() {
        DifyProperties.Hosted hosted = difyProperties.getHosted();

        record ProviderEntry(String value, Supplier<DifyProperties.HostedProvider> accessor) {}

        List<ProviderEntry> providers = List.of(
                new ProviderEntry("langgenius/openai/openai", hosted::getOpenai),
                new ProviderEntry("langgenius/anthropic/anthropic", hosted::getAnthropic),
                new ProviderEntry("langgenius/gemini/google", hosted::getGemini),
                new ProviderEntry("langgenius/x/x", hosted::getXai),
                new ProviderEntry("langgenius/deepseek/deepseek", hosted::getDeepseek),
                new ProviderEntry("langgenius/tongyi/tongyi", hosted::getTongyi));

        List<String> result = new ArrayList<>();
        for (ProviderEntry entry : providers) {
            DifyProperties.HostedProvider provider = entry.accessor().get();
            if (provider.isPaidEnabled() && provider.isTrialEnabled()) {
                result.add(entry.value());
            }
        }
        return result;
    }

    // ---- billing / enterprise stubs (SELF_HOSTED no-ops) -----------------------

    /**
     * Resolve whether human-input email delivery is enabled.
     * Mirrors Python's {@code _resolve_human_input_email_delivery_enabled}.
     *
     * <ul>
     *   <li>Enterprise enabled OR billing disabled → true</li>
     *   <li>Billing enabled without tenant → false</li>
     *   <li>Billing enabled with tenant → check plan (Professional/Team)</li>
     * </ul>
     */
    private boolean resolveHumanInputEmailDeliveryEnabled(FeatureModel features, String tenantId) {
        if (difyProperties.getEnterprise().isEnabled() || !difyProperties.getBilling().isEnabled()) {
            return true;
        }
        if (tenantId == null || tenantId.isBlank()) {
            return false;
        }
        // When billing is enabled, check subscription plan (BillingModel/SubscriptionModel are records)
        return features.getBilling().enabled()
                && ("professional".equals(features.getBilling().subscription().plan())
                        || "team".equals(features.getBilling().subscription().plan()));
    }

    /**
     * Enrich features from the billing API.
     * Placeholder for future billing integration (Slice N).
     */
    private void fulfillParamsFromBillingApi(FeatureModel features, String tenantId) {
        // TODO: call BillingService.getInfo(tenantId) and map response fields
        logger.debug("Billing enrichment skipped — not yet implemented (tenantId={})", tenantId);
    }

    /**
     * Enrich features from enterprise workspace info.
     * Placeholder for future enterprise integration (Slice N).
     */
    private void fulfillParamsFromWorkspaceInfo(FeatureModel features, String tenantId) {
        // TODO: call EnterpriseService.getWorkspaceInfo(tenantId) and map WorkspaceMembers
        logger.debug("Enterprise workspace enrichment skipped — not yet implemented (tenantId={})", tenantId);
    }

    /**
     * Enrich system features from enterprise info.
     * Placeholder for future enterprise integration (Slice N).
     */
    private void fulfillParamsFromEnterprise(SystemFeatureModel systemFeatures, boolean isAuthenticated) {
        // TODO: call EnterpriseService.getInfo() and map SSO, branding, webapp auth, license, etc.
        logger.debug("Enterprise system enrichment skipped — not yet implemented");
    }
}
