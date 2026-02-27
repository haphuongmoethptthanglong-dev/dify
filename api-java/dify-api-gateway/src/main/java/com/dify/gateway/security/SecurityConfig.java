package com.dify.gateway.security;

import com.dify.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration replicating Python's Flask-Login auth dispatch.
 *
 * The Python API dispatches auth by blueprint name (ext_login.py request_loader):
 * - console, inner_api: JWT with user_id claim
 * - web: JWT passport with end_user_id claim
 * - service_api: Bearer API token
 * - files, mcp, trigger: mixed/special auth
 *
 * Each URL pattern gets its own SecurityFilterChain with appropriate auth.
 * All chains enable {@code .cors(Customizer.withDefaults())} to pick up the
 * {@code CorsConfigurationSource} bean from CorsConfig, ensuring CORS preflight
 * (OPTIONS) requests pass through without auth.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;
    private final OptionalJwtFilter optionalJwtFilter;

    public SecurityConfig(JwtTokenService jwtTokenService, ObjectMapper objectMapper,
                          OptionalJwtFilter optionalJwtFilter) {
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.optionalJwtFilter = optionalJwtFilter;
    }

    /**
     * Health and public endpoints — no auth required.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/health", "/health/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Console bootstrap endpoints — unauthenticated by design.
     *
     * These endpoints must be accessible before any admin account exists
     * (first-time setup, health check, version info, init validation).
     * Matches Python's unauthenticated root controllers in controllers/console/.
     *
     * {@code /console/api/system-features} uses {@link OptionalJwtFilter} to detect
     * authenticated callers without requiring auth (license info in enterprise mode).
     */
    @Bean
    @Order(2)
    public SecurityFilterChain consoleBootstrapFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/console/api/setup",
                        "/console/api/ping",
                        "/console/api/version",
                        "/console/api/init",
                        "/console/api/system-features")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(optionalJwtFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Console API filter chain — JWT auth with user_id claim.
     * Matches Python blueprint 'console' at /console/api/**.
     */
    @Bean
    @Order(3)
    public SecurityFilterChain consoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/console/api/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    var errorResponse = new ErrorResponse("unauthorized", "Unauthorized.", 401);
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }));
        return http.build();
    }

    /**
     * Service API filter chain — Bearer API token auth.
     * Matches Python blueprint 'service_api' at /v1/**.
     * TODO: Implement ApiTokenAuthenticationFilter when migrating service API slice.
     */
    @Bean
    @Order(4)
    public SecurityFilterChain serviceApiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/v1/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    var errorResponse = new ErrorResponse("unauthorized", "Unauthorized.", 401);
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }));
        return http.build();
    }

    /**
     * Web API filter chain — JWT passport with end_user_id claim.
     * Matches Python blueprint 'web' at /api/**.
     * TODO: Implement WebPassportAuthenticationFilter when migrating web API slice.
     */
    @Bean
    @Order(5)
    public SecurityFilterChain webApiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    var errorResponse = new ErrorResponse("unauthorized", "Unauthorized.", 401);
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }));
        return http.build();
    }

    /**
     * Default filter chain — catch-all for other paths (files, inner_api, mcp, trigger).
     */
    @Bean
    @Order(6)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
