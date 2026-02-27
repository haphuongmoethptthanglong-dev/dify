package com.dify.boot.contract;

import static org.assertj.core.api.Assertions.assertThat;

import com.dify.gateway.security.JwtTokenService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Contract tests for Slice 2 — Feature Flags / System Info endpoints.
 *
 * Verifies that the Java responses match the Python API contract exactly.
 * Runs against real PostgreSQL and Redis (same Docker infra as dev).
 *
 * Endpoint coverage:
 * <ul>
 *   <li>GET /console/api/system-features — unauthenticated (optional JWT)</li>
 *   <li>GET /console/api/features — authenticated, tenant-scoped</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsoleFeatureContractTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute("DELETE FROM tenant_account_joins");
        jdbcTemplate.execute("DELETE FROM dify_setups");
        jdbcTemplate.execute("DELETE FROM accounts");
        jdbcTemplate.execute("DELETE FROM tenants");
        var keys = redisTemplate.keys("init_validated:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ---- GET /console/api/system-features (unauthenticated) ----

    @Test
    @Order(1)
    @SuppressWarnings("unchecked")
    void systemFeatures_unauthenticated_returnsFullContract() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/console/api/system-features", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();

        // Top-level flags
        assertThat(body.get("sso_enforced_for_signin")).isEqualTo(false);
        assertThat(body.get("sso_enforced_for_signin_protocol")).isEqualTo("");
        assertThat(body.get("enable_marketplace")).isEqualTo(false);
        assertThat(body.get("max_plugin_package_size")).isEqualTo(10485760);
        assertThat(body.get("enable_email_code_login")).isEqualTo(false);
        assertThat(body.get("enable_email_password_login")).isEqualTo(true);
        assertThat(body.get("enable_social_oauth_login")).isEqualTo(false);

        // is_ prefixed booleans — critical contract parity
        assertThat(body.get("is_allow_register")).isEqualTo(false);
        assertThat(body.get("is_allow_create_workspace")).isEqualTo(false);
        assertThat(body.get("is_email_setup")).isEqualTo(false);

        // License sub-object
        Map<String, Object> license = (Map<String, Object>) body.get("license");
        assertThat(license).isNotNull();
        assertThat(license.get("status")).isEqualTo("none");
        assertThat(license.get("expired_at")).isEqualTo("");

        // Branding sub-object
        Map<String, Object> branding = (Map<String, Object>) body.get("branding");
        assertThat(branding).isNotNull();
        assertThat(branding.get("enabled")).isEqualTo(false);

        // WebApp auth sub-object
        Map<String, Object> webappAuth = (Map<String, Object>) body.get("webapp_auth");
        assertThat(webappAuth).isNotNull();
        assertThat(webappAuth.get("enabled")).isEqualTo(false);

        // Plugin installation permission sub-object
        Map<String, Object> pluginPermission = (Map<String, Object>) body.get("plugin_installation_permission");
        assertThat(pluginPermission).isNotNull();

        // Plugin manager sub-object
        Map<String, Object> pluginManager = (Map<String, Object>) body.get("plugin_manager");
        assertThat(pluginManager).isNotNull();
        assertThat(pluginManager.get("enabled")).isEqualTo(false);

        // Other flags
        assertThat(body.get("enable_change_email")).isEqualTo(true);
        assertThat(body.get("trial_models")).isEqualTo(List.of());
        assertThat(body.get("enable_trial_app")).isEqualTo(false);
        assertThat(body.get("enable_explore_banner")).isEqualTo(false);
    }

    @Test
    @Order(2)
    void systemFeatures_noExtraFields() {
        // Verify no unexpected fields leak into the response
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/console/api/system-features", Map.class);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        // Must NOT contain raw boolean getter artifacts (e.g., "allow_register" without "is_" prefix)
        assertThat(body).doesNotContainKey("allow_register");
        assertThat(body).doesNotContainKey("allow_create_workspace");
        assertThat(body).doesNotContainKey("email_setup");
    }

    // ---- GET /console/api/features (authenticated) ----

    @Test
    @Order(10)
    @SuppressWarnings("unchecked")
    void features_authenticated_returnsFullContract() {
        // Arrange: create account + tenant + join + setup via JDBC
        UUID accountId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID joinId = UUID.randomUUID();

        jdbcTemplate.update(
                "INSERT INTO accounts (id, name, email, password, password_salt, status, "
                        + "interface_language, interface_theme, timezone, last_active_at, "
                        + "initialized_at, created_at, updated_at) "
                        + "VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW(), NOW())",
                accountId.toString(), "TestUser", "feature-test@example.com",
                "hashed", "salt", "active", "en-US", "light", "UTC");

        jdbcTemplate.update(
                "INSERT INTO tenants (id, name, status, plan, created_at, updated_at) "
                        + "VALUES (?::uuid, ?, ?, ?, NOW(), NOW())",
                tenantId.toString(), "TestWorkspace", "normal", "basic");

        jdbcTemplate.update(
                "INSERT INTO tenant_account_joins (id, tenant_id, account_id, role, \"current\", "
                        + "created_at, updated_at) "
                        + "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, NOW(), NOW())",
                joinId.toString(), tenantId.toString(), accountId.toString(), "owner", true);

        jdbcTemplate.execute(
                "INSERT INTO dify_setups (version, setup_at) VALUES ('0.15.3-test', NOW())");

        // Issue JWT for this account
        String token = jwtTokenService.issue(Map.of(
                "user_id", accountId.toString(),
                "exp", Instant.now().plusSeconds(3600).getEpochSecond()));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Act
        ResponseEntity<Map> response = restTemplate.exchange(
                "/console/api/features", HttpMethod.GET,
                new HttpEntity<>(headers), Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();

        // Billing sub-object (SELF_HOSTED defaults)
        Map<String, Object> billing = (Map<String, Object>) body.get("billing");
        assertThat(billing).isNotNull();
        assertThat(billing.get("enabled")).isEqualTo(false);
        Map<String, Object> subscription = (Map<String, Object>) billing.get("subscription");
        assertThat(subscription.get("plan")).isEqualTo("sandbox");
        assertThat(subscription.get("interval")).isEqualTo("");

        // Education
        Map<String, Object> education = (Map<String, Object>) body.get("education");
        assertThat(education).isNotNull();
        assertThat(education.get("enabled")).isEqualTo(false);
        assertThat(education.get("activated")).isEqualTo(false);

        // Limitation models
        Map<String, Object> members = (Map<String, Object>) body.get("members");
        assertThat(members).containsEntry("size", 0).containsEntry("limit", 1);

        Map<String, Object> apps = (Map<String, Object>) body.get("apps");
        assertThat(apps).containsEntry("size", 0).containsEntry("limit", 10);

        Map<String, Object> vectorSpace = (Map<String, Object>) body.get("vector_space");
        assertThat(vectorSpace).containsEntry("size", 0).containsEntry("limit", 5);

        assertThat(body.get("knowledge_rate_limit")).isEqualTo(10);

        Map<String, Object> annotationQuota = (Map<String, Object>) body.get("annotation_quota_limit");
        assertThat(annotationQuota).containsEntry("size", 0).containsEntry("limit", 10);

        Map<String, Object> docsQuota = (Map<String, Object>) body.get("documents_upload_quota");
        assertThat(docsQuota).containsEntry("size", 0).containsEntry("limit", 50);

        assertThat(body.get("docs_processing")).isEqualTo("standard");

        // Feature flags
        assertThat(body.get("can_replace_logo")).isEqualTo(false);
        assertThat(body.get("model_load_balancing_enabled")).isEqualTo(false);
        assertThat(body.get("dataset_operator_enabled")).isEqualTo(false);
        assertThat(body.get("webapp_copyright_enabled")).isEqualTo(false);

        // Workspace members (LicenseLimitationModel — no "available" field)
        Map<String, Object> workspaceMembers = (Map<String, Object>) body.get("workspace_members");
        assertThat(workspaceMembers).containsEntry("enabled", false);
        assertThat(workspaceMembers).containsEntry("size", 0);
        assertThat(workspaceMembers).containsEntry("limit", 0);
        assertThat(workspaceMembers).doesNotContainKey("available");

        // is_ prefixed boolean
        assertThat(body.get("is_allow_transfer_workspace")).isEqualTo(true);

        // Quota models
        Map<String, Object> triggerEvent = (Map<String, Object>) body.get("trigger_event");
        assertThat(triggerEvent).containsEntry("usage", 0).containsEntry("limit", 3000);

        Map<String, Object> apiRateLimit = (Map<String, Object>) body.get("api_rate_limit");
        assertThat(apiRateLimit).containsEntry("usage", 0).containsEntry("limit", 5000);

        // SELF_HOSTED with no billing → human_input_email_delivery_enabled = true
        assertThat(body.get("human_input_email_delivery_enabled")).isEqualTo(true);

        // Knowledge pipeline
        Map<String, Object> knowledgePipeline = (Map<String, Object>) body.get("knowledge_pipeline");
        assertThat(knowledgePipeline).containsEntry("publish_enabled", false);

        assertThat(body.get("next_credit_reset_date")).isEqualTo(0);
    }

    @Test
    @Order(11)
    void features_unauthenticated_returns401() {
        // Setup must exist or SetupRequiredInterceptor blocks first
        jdbcTemplate.execute(
                "INSERT INTO dify_setups (version, setup_at) VALUES ('0.15.3-test', NOW())");

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/console/api/features", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(12)
    void features_noSetup_returns401() {
        // No DifySetup record → SetupRequiredInterceptor blocks
        UUID accountId = UUID.randomUUID();
        String token = jwtTokenService.issue(Map.of(
                "user_id", accountId.toString(),
                "exp", Instant.now().plusSeconds(3600).getEpochSecond()));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/console/api/features", HttpMethod.GET,
                new HttpEntity<>(headers), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
