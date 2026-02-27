package com.dify.gateway.config;

import com.dify.gateway.interceptor.AccountInitializationInterceptor;
import com.dify.gateway.interceptor.SetupRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers console-chain interceptors that mirror Python's decorator stack.
 *
 * <p>Ordering matches the Python decorator application order:
 * {@code @setup_required} runs first, then {@code @account_initialization_required}.
 *
 * <p>Both interceptors apply to {@code /console/api/**} but exclude bootstrap
 * endpoints (setup, ping, version, init, system-features) which are unauthenticated.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SetupRequiredInterceptor setupRequiredInterceptor;
    private final AccountInitializationInterceptor accountInitializationInterceptor;

    public WebMvcConfig(SetupRequiredInterceptor setupRequiredInterceptor,
                        AccountInitializationInterceptor accountInitializationInterceptor) {
        this.setupRequiredInterceptor = setupRequiredInterceptor;
        this.accountInitializationInterceptor = accountInitializationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // SetupRequired runs first — mirrors Python's @setup_required decorator
        registry.addInterceptor(setupRequiredInterceptor)
                .addPathPatterns("/console/api/**")
                .excludePathPatterns(
                        "/console/api/setup",
                        "/console/api/ping",
                        "/console/api/version",
                        "/console/api/init",
                        "/console/api/system-features")
                .order(1);

        // AccountInitialization runs second — mirrors @account_initialization_required
        registry.addInterceptor(accountInitializationInterceptor)
                .addPathPatterns("/console/api/**")
                .excludePathPatterns(
                        "/console/api/setup",
                        "/console/api/ping",
                        "/console/api/version",
                        "/console/api/init",
                        "/console/api/system-features")
                .order(2);
    }
}
