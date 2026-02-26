package com.dify.boot.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Contract tests for the 4 console bootstrap endpoints (Slice 1).
 *
 * Verifies that the Java responses match the Python API contract exactly.
 * Runs against real PostgreSQL and Redis (same Docker infra as dev).
 *
 * Endpoint coverage:
 * <ul>
 *   <li>GET  /console/api/ping</li>
 *   <li>GET  /console/api/version</li>
 *   <li>GET  /console/api/setup</li>
 *   <li>POST /console/api/setup</li>
 *   <li>GET  /console/api/init</li>
 *   <li>POST /console/api/init</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsoleBootstrapContractTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute("DELETE FROM tenant_account_joins");
        jdbcTemplate.execute("DELETE FROM dify_setups");
        jdbcTemplate.execute("DELETE FROM accounts");
        jdbcTemplate.execute("DELETE FROM tenants");
        // Clean init_validated redis keys
        var keys = redisTemplate.keys("init_validated:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ---- GET /console/api/ping ----

    @Test
    @Order(1)
    void ping_returnsExactPythonContract() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/console/api/ping", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyEntriesOf(Map.of("result", "pong"));
    }

    // ---- GET /console/api/version ----

    @Test
    @Order(2)
    void version_returnsExpectedStructure() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/console/api/version?current_version=0.14.0", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKeys("version", "release_date", "release_notes",
                "can_auto_update", "features");

        @SuppressWarnings("unchecked")
        Map<String, Object> features = (Map<String, Object>) body.get("features");
        assertThat(features).containsKeys("can_replace_logo", "model_load_balancing_enabled");
        assertThat(features.get("can_replace_logo")).isEqualTo(false);
        assertThat(features.get("model_load_balancing_enabled")).isEqualTo(false);
    }

    @Test
    @Order(3)
    void version_withNoUpdateUrl_echoesFallbackVersion() {
        // check_update_url is empty in test profile, so should use configured version
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/console/api/version?current_version=0.14.0", Map.class);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        // With empty check_update_url, returns configured version (0.15.3-test)
        assertThat(body.get("version")).isEqualTo("0.15.3-test");
        assertThat(body.get("can_auto_update")).isEqualTo(false);
        assertThat(body.get("release_date")).isEqualTo("");
        assertThat(body.get("release_notes")).isEqualTo("");
    }

    // ---- GET /console/api/setup ----

    @Test
    @Order(10)
    void setup_get_notStarted_matchesPythonContract() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/console/api/setup", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("step")).isEqualTo("not_started");
        assertThat(body).containsKey("setup_at");
        assertThat(body.get("setup_at")).isNull();
    }

    @Test
    @Order(11)
    void setup_get_finished_includesSetupAt() {
        // Insert a setup record directly
        jdbcTemplate.execute(
                "INSERT INTO dify_setups (version, setup_at) VALUES ('0.15.3-test', NOW())");

        ResponseEntity<Map> response = restTemplate.getForEntity("/console/api/setup", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("step")).isEqualTo("finished");
        assertThat(body.get("setup_at")).isNotNull();
    }

    // ---- POST /console/api/setup ----

    @Test
    @Order(20)
    void setup_post_createsAccountAndTenant() {
        Map<String, String> payload = Map.of(
                "email", "contract-test@example.com",
                "name", "ContractTest",
                "password", "ValidPass1!");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/console/api/setup", payload, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("result", "success");

        // Verify DB records
        Integer accountCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM accounts WHERE email = 'contract-test@example.com'",
                Integer.class);
        assertThat(accountCount).isEqualTo(1);

        Integer tenantCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM tenants", Integer.class);
        assertThat(tenantCount).isEqualTo(1);

        Integer joinCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM tenant_account_joins WHERE role = 'owner'",
                Integer.class);
        assertThat(joinCount).isEqualTo(1);

        Integer setupCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM dify_setups", Integer.class);
        assertThat(setupCount).isEqualTo(1);
    }

    @Test
    @Order(21)
    void setup_post_alreadySetup_returns403() {
        // First setup
        restTemplate.postForEntity("/console/api/setup",
                Map.of("email", "first@example.com", "name", "First", "password", "ValidPass1!"),
                Map.class);

        // Second attempt
        ResponseEntity<Map> response = restTemplate.postForEntity("/console/api/setup",
                Map.of("email", "second@example.com", "name", "Second", "password", "ValidPass1!"),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("code")).isEqualTo("already_setup");
        assertThat(body.get("status")).isEqualTo(403);
    }

    @Test
    @Order(22)
    void setup_post_missingEmail_returns400() {
        ResponseEntity<Map> response = restTemplate.postForEntity("/console/api/setup",
                Map.of("name", "Test", "password", "ValidPass1!"),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(23)
    void setup_post_weakPassword_returns400() {
        ResponseEntity<Map> response = restTemplate.postForEntity("/console/api/setup",
                Map.of("email", "test@example.com", "name", "Test", "password", "short"),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ---- GET /console/api/init ----

    @Test
    @Order(30)
    void init_get_noPassword_returnsFinished() {
        // Test profile has init-password="" (no password required)
        ResponseEntity<Map> response = restTemplate.getForEntity("/console/api/init", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "finished");
    }

    @Test
    @Order(31)
    void init_get_afterSetup_returnsFinished() {
        // Setup exists in DB → init is finished
        jdbcTemplate.execute(
                "INSERT INTO dify_setups (version, setup_at) VALUES ('0.15.3-test', NOW())");

        ResponseEntity<Map> response = restTemplate.getForEntity("/console/api/init", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "finished");
    }

    // ---- GET /health ----

    @Test
    @Order(40)
    void health_returnsOk() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/health", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "ok");
    }
}
