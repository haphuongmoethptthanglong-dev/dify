package com.dify.boot.config;

import com.dify.common.constants.DifyHeaders;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS configuration replicating Python's ext_blueprints.py per-path policies.
 *
 * Exposes a {@link CorsConfigurationSource} bean consumed by Spring Security's
 * {@code .cors(Customizer.withDefaults())} so that CORS preflight (OPTIONS)
 * requests pass through the security filter chain correctly.
 *
 * Console: CONSOLE_CORS_ALLOW_ORIGINS, credentials=true
 * Service API: all origins, no credentials
 * Web: WEB_API_CORS_ALLOW_ORIGINS, credentials=true
 * Files: all origins
 */
@Configuration
public class CorsConfig {

    @Value("${dify.cors.console-origins:*}")
    private String consoleOrigins;

    @Value("${dify.cors.web-origins:*}")
    private String webOrigins;

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "PUT", "POST", "DELETE", "OPTIONS", "PATCH");

    private static final List<String> EXPOSED_HEADERS = List.of(
            DifyHeaders.X_VERSION, DifyHeaders.X_ENV, DifyHeaders.X_TRACE_ID);

    private static final List<String> AUTHENTICATED_HEADERS = List.of(
            "Content-Type", DifyHeaders.X_APP_CODE, DifyHeaders.X_PASSPORT,
            "Authorization", DifyHeaders.X_CSRF_TOKEN);

    private static final List<String> SERVICE_API_HEADERS = List.of(
            "Content-Type", DifyHeaders.X_APP_CODE, DifyHeaders.X_PASSPORT, "Authorization");

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();

        // Console: /console/api/**
        var consoleCors = new CorsConfiguration();
        consoleCors.setAllowedOriginPatterns(parseOrigins(consoleOrigins));
        consoleCors.setAllowCredentials(true);
        consoleCors.setAllowedMethods(ALLOWED_METHODS);
        consoleCors.setAllowedHeaders(AUTHENTICATED_HEADERS);
        consoleCors.setExposedHeaders(EXPOSED_HEADERS);
        source.registerCorsConfiguration("/console/api/**", consoleCors);

        // Service API: /v1/**
        var serviceApiCors = new CorsConfiguration();
        serviceApiCors.setAllowedOriginPatterns(List.of("*"));
        serviceApiCors.setAllowCredentials(false);
        serviceApiCors.setAllowedMethods(ALLOWED_METHODS);
        serviceApiCors.setAllowedHeaders(SERVICE_API_HEADERS);
        serviceApiCors.setExposedHeaders(EXPOSED_HEADERS);
        source.registerCorsConfiguration("/v1/**", serviceApiCors);

        // Web API: /api/**
        var webCors = new CorsConfiguration();
        webCors.setAllowedOriginPatterns(parseOrigins(webOrigins));
        webCors.setAllowCredentials(true);
        webCors.setAllowedMethods(ALLOWED_METHODS);
        webCors.setAllowedHeaders(AUTHENTICATED_HEADERS);
        webCors.setExposedHeaders(EXPOSED_HEADERS);
        source.registerCorsConfiguration("/api/**", webCors);

        // Files: /files/**
        var filesCors = new CorsConfiguration();
        filesCors.setAllowedOriginPatterns(List.of("*"));
        filesCors.setAllowCredentials(false);
        filesCors.setAllowedMethods(ALLOWED_METHODS);
        filesCors.setAllowedHeaders(List.of(
                "Content-Type", DifyHeaders.X_APP_CODE, DifyHeaders.X_PASSPORT, DifyHeaders.X_CSRF_TOKEN));
        filesCors.setExposedHeaders(EXPOSED_HEADERS);
        source.registerCorsConfiguration("/files/**", filesCors);

        return source;
    }

    private List<String> parseOrigins(String origins) {
        if (origins == null || origins.isBlank() || "*".equals(origins)) {
            return List.of("*");
        }
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
