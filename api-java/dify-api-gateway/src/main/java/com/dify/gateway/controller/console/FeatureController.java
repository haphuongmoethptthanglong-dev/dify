package com.dify.gateway.controller.console;

import com.dify.billing.model.FeatureModel;
import com.dify.billing.model.SystemFeatureModel;
import com.dify.gateway.interceptor.AccountInitializationInterceptor;
import com.dify.gateway.security.DifyAuthenticationToken;
import com.dify.gateway.service.FeatureService;
import com.dify.iam.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feature flag endpoints for the console dashboard.
 *
 * Matches Python's {@code controllers/console/feature.py}.
 *
 * <ul>
 *   <li>{@code GET /console/api/features} — authenticated, tenant-scoped</li>
 *   <li>{@code GET /console/api/system-features} — unauthenticated (optional JWT)</li>
 * </ul>
 */
@RestController
@RequestMapping("/console/api")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    /**
     * Get feature configuration for the current tenant.
     *
     * Protected by the console security chain (JWT required) plus
     * {@code SetupRequiredInterceptor} and {@code AccountInitializationInterceptor}.
     * The account is loaded by the interceptor and stored as a request attribute.
     *
     * Python decorators: @setup_required, @login_required,
     * @account_initialization_required, @cloud_utm_record
     */
    @GetMapping("/features")
    public FeatureModel getFeatures(HttpServletRequest request) {
        Account account = (Account) request.getAttribute(AccountInitializationInterceptor.CURRENT_ACCOUNT_ATTR);
        String tenantId = account != null && account.getCurrentTenantId() != null
                ? account.getCurrentTenantId().toString()
                : null;
        return featureService.getFeatures(tenantId);
    }

    /**
     * Get system-wide feature configuration.
     *
     * Unauthenticated by design — the dashboard needs this data before login.
     * Optional JWT is evaluated by {@code OptionalJwtFilter} to determine
     * whether to include license info (enterprise only).
     *
     * Python: no decorators, but uses try/catch on current_user.is_authenticated.
     */
    @GetMapping("/system-features")
    public SystemFeatureModel getSystemFeatures() {
        boolean isAuthenticated = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof DifyAuthenticationToken token && token.isAuthenticated()) {
            isAuthenticated = true;
        }
        return featureService.getSystemFeatures(isAuthenticated);
    }
}
