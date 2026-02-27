# Handoff: Migrate Test Infrastructure to Testcontainers

**Date:** 2026-02-27
**Branch:** feat/port-to-java-oc
**Commit:** ac1e1d34e1 (latest: post-Slice 2 review commit)

## Context

Port-to-Java project. 2 slices done (Console Bootstrap, Feature Flags). Next task before Slice 3A (Core Auth) is migrating test infra from hardcoded Docker IPs to Testcontainers for portable, CI-friendly tests.

## Current State (surveyed)

- **2 test files** exist:
  - `api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleBootstrapContractTest.java`
  - `api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleFeatureContractTest.java`
- Both use `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@ActiveProfiles("test")`
- **No base test class** exists
- **No Testcontainers dependency** anywhere
- `application-test.yml` has **hardcoded Docker IPs**:
  - DB: `172.19.0.8:5432` (env var fallback: `DB_HOST`)
  - Redis: `172.19.0.2:6379` (env var fallback: `REDIS_HOST`)
  - Passwords: `difyai123456`
- Tests clean state via direct JDBC `DELETE` statements
- `spring-boot-starter-test` is the only test dependency in `dify-api-boot/pom.xml`

## Done

- Nothing yet for this task — this is the starting handoff

## Remaining (Implementation Steps)

1. **Add Testcontainers BOM + dependencies to parent POM** (`api-java/pom.xml`):
   - `testcontainers-bom` in `<dependencyManagement>`
   - `org.testcontainers:postgresql` (test scope)
   - `org.testcontainers:junit-jupiter` (test scope)
   - Consider `com.redis:testcontainers-redis` or `org.testcontainers:GenericContainer` for Redis

2. **Add Testcontainers deps to `dify-api-boot/pom.xml`**:
   - `org.testcontainers:postgresql`
   - `org.testcontainers:junit-jupiter`

3. **Create abstract base test class** (e.g., `AbstractIntegrationTest.java` in dify-api-boot test sources):
   - `@Testcontainers` annotation
   - `@Container static PostgreSQLContainer<?>` with image `postgres:15` (match prod)
   - `@Container static GenericContainer<?>` for Redis with image `redis:7`
   - `@DynamicPropertySource` to inject JDBC URL, username, password, Redis host/port
   - Consider Flyway auto-migration (already configured in main app)

4. **Update `application-test.yml`**:
   - Remove hardcoded IPs — Testcontainers `@DynamicPropertySource` overrides them
   - Keep non-connection config (logging, JWT secret, etc.)

5. **Refactor both existing tests** to extend `AbstractIntegrationTest`:
   - `ConsoleBootstrapContractTest` → extends `AbstractIntegrationTest`
   - `ConsoleFeatureContractTest` → extends `AbstractIntegrationTest`
   - Remove any per-test DB/Redis setup that's now handled by base class

6. **Verify**: Run `mvn test -pl dify-api-boot` — both tests must pass with TC containers

## Key Decisions to Make

- **Redis container**: Use `GenericContainer("redis:7")` (simpler) or dedicated `RedisContainer`?
  - Recommendation: `GenericContainer` — fewer dependencies, sufficient for integration tests
- **Container lifecycle**: `static` containers shared across all tests (faster) vs per-test (isolated)?
  - Recommendation: static shared — existing tests already do manual cleanup via DELETE
- **Image versions**: Match production Docker Compose (`postgres:15`, `redis:7`)

## Risks

- R16 (from plan): Docker IP hardcoding already broke once — this task directly mitigates it
- Testcontainers requires Docker daemon running on test machine
- Flyway migrations must work on clean TC database (should be fine — they run on app startup)

## Files to Touch

- `api-java/pom.xml` — add TC BOM
- `api-java/dify-api-boot/pom.xml` — add TC test deps
- `api-java/dify-api-boot/src/test/java/com/dify/boot/contract/AbstractIntegrationTest.java` — NEW base class
- `api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleBootstrapContractTest.java` — extend base
- `api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleFeatureContractTest.java` — extend base
- `api-java/dify-api-boot/src/test/resources/application-test.yml` — remove hardcoded IPs

## Resume Instructions

1. Read this handoff
2. Read plan file `notes/plan26-02-2026.md` Section 1F (test strategy) and Section 2B (test infra prereq)
3. Implement steps 1-6 above
4. Run `cd api-java && mvn test -pl dify-api-boot` to verify
5. Commit with `test(infra): migrate to Testcontainers for PostgreSQL and Redis`
