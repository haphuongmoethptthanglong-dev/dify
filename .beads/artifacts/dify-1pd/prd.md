# Beads PRD: Migrate Test Infrastructure to Testcontainers

**Bead:** dify-1pd  
**Created:** 2026-02-27  
**Status:** Draft

## Bead Metadata

```yaml
depends_on: []
parallel: true
conflicts_with: []
blocks: []
estimated_hours: 2
```

---

## Problem Statement

### What problem are we solving?

The integration test infrastructure in `api-java` uses hardcoded Docker network IPs (`172.19.0.8` for PostgreSQL, `172.19.0.2` for Redis) in `application-test.yml`. These IPs are specific to the developer's local Docker network and break on any machine with a different network topology, including CI runners. Tests cannot run portably.

### Why now?

This is a prerequisite for Slice 3A (Core Auth) of the port-to-Java project. As more contract tests are added, every new test inherits the broken infrastructure. The hardcoded IPs have already broken once (R16 from the project plan). Fixing this now prevents compounding tech debt.

### Who is affected?

- **Primary users:** Backend developers writing and running Java integration tests
- **Secondary users:** CI/CD pipelines executing test suites

---

## Scope

### In-Scope

- Add Testcontainers BOM and dependencies to Maven POMs
- Create shared abstract base test class with PostgreSQL and Redis containers
- Wire containers via `@DynamicPropertySource` to override Spring properties
- Remove hardcoded Docker IPs from `application-test.yml`
- Refactor 2 existing contract tests to extend the base class
- Verify all tests pass with Testcontainers

### Out-of-Scope

- Adding new test cases (only migrating existing infrastructure)
- Changing production Docker Compose files
- Testcontainers for services beyond PostgreSQL and Redis
- Performance benchmarking of container startup times
- CI pipeline configuration changes (Docker-in-Docker / DinD setup)

---

## Proposed Solution

### Overview

Introduce Testcontainers as the test container lifecycle manager. A shared `AbstractIntegrationTest` base class manages static PostgreSQL and Redis containers that are started once and reused across all test classes in the module. Spring properties are injected dynamically via `@DynamicPropertySource`, eliminating all hardcoded connection strings. Existing contract tests extend this base class instead of configuring infrastructure independently.

---

## Requirements

### Functional Requirements

#### Testcontainers Dependencies

Testcontainers BOM and module dependencies are available in the Maven build.

**Scenarios:**

- **WHEN** `mvn dependency:tree -pl dify-api-boot` is run **THEN** `org.testcontainers:postgresql` and `org.testcontainers:junit-jupiter` appear in test scope
- **WHEN** a test class uses `@Testcontainers` annotation **THEN** it compiles and resolves without errors

#### Shared Container Lifecycle

A single pair of containers (PostgreSQL + Redis) is shared across all test classes in the module.

**Scenarios:**

- **WHEN** multiple test classes extend `AbstractIntegrationTest` **THEN** containers start only once per JVM (static lifecycle)
- **WHEN** containers start **THEN** they use images matching production: `postgres:15-alpine` and `redis:6-alpine`
- **WHEN** Flyway runs on the Testcontainers PostgreSQL **THEN** `V0__baseline.sql` migration applies successfully

#### Dynamic Property Injection

Spring datasource and Redis properties are injected from running containers, not from config files.

**Scenarios:**

- **WHEN** `AbstractIntegrationTest` starts **THEN** `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` are overridden with Testcontainers values
- **WHEN** `AbstractIntegrationTest` starts **THEN** `spring.data.redis.host` and `spring.data.redis.port` are overridden with Testcontainers values
- **WHEN** `application-test.yml` is loaded **THEN** no hardcoded Docker network IPs remain in connection properties

#### Existing Tests Pass

Both contract test classes work identically after migration.

**Scenarios:**

- **WHEN** `ConsoleBootstrapContractTest` runs **THEN** all existing assertions pass
- **WHEN** `ConsoleFeatureContractTest` runs **THEN** all existing assertions pass
- **WHEN** tests run on a machine without the old Docker network **THEN** they still pass

### Non-Functional Requirements

- **Performance:** Container startup adds ~5-10s to first test class; acceptable for integration tests
- **Compatibility:** Requires Docker daemon available on test machine (standard for Java integration testing)
- **Reliability:** Static containers avoid per-test startup overhead; matches current manual cleanup pattern

---

## Success Criteria

- [ ] `AbstractIntegrationTest.java` exists and manages PostgreSQL + Redis containers
  - Verify: `test -f api-java/dify-api-boot/src/test/java/com/dify/boot/contract/AbstractIntegrationTest.java`
- [ ] No hardcoded Docker IPs in `application-test.yml` connection properties
  - Verify: `! grep -E '172\.\d+\.\d+\.\d+' api-java/dify-api-boot/src/test/resources/application-test.yml`
- [ ] Testcontainers dependencies present in POMs
  - Verify: `grep -q 'testcontainers' api-java/pom.xml && grep -q 'testcontainers' api-java/dify-api-boot/pom.xml`
- [ ] Both contract tests extend `AbstractIntegrationTest`
  - Verify: `grep -l 'extends AbstractIntegrationTest' api-java/dify-api-boot/src/test/java/com/dify/boot/contract/*ContractTest.java | wc -l` returns 2
- [ ] All tests pass
  - Verify: `cd api-java && mvn test -pl dify-api-boot`

---

## Technical Context

### Existing Patterns

- `ConsoleBootstrapContractTest.java` — Uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@ActiveProfiles("test")` + `TestRestTemplate` for HTTP assertions + `JdbcTemplate`/`StringRedisTemplate` for cleanup
- `ConsoleFeatureContractTest.java` — Same pattern plus `JwtTokenService` for authenticated endpoint testing
- `application-test.yml` — Spring properties with `${ENV_VAR:default}` fallback pattern for connection config

### Key Files

- `api-java/pom.xml` — Parent POM with `<dependencyManagement>`, Spring Boot 3.4.3 parent, Java 21
- `api-java/dify-api-boot/pom.xml` — Boot module POM, currently only `spring-boot-starter-test` for test deps, includes `flyway-core` + `flyway-database-postgresql`
- `api-java/dify-api-boot/src/test/resources/application-test.yml` — Test config with hardcoded IPs (`172.19.0.8:5432`, `172.19.0.2:6379`), Flyway enabled with `baseline-on-migrate: true`
- `docker/docker-compose.yaml` — Production images: `postgres:15-alpine` (line 858), `redis:6-alpine` (line 925)

### Affected Files

Files this bead will modify (for conflict detection):

```yaml
files:
  - api-java/pom.xml # Add Testcontainers BOM to dependencyManagement
  - api-java/dify-api-boot/pom.xml # Add TC postgresql + junit-jupiter test deps
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/AbstractIntegrationTest.java # NEW: shared base test class
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleBootstrapContractTest.java # Extend AbstractIntegrationTest
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleFeatureContractTest.java # Extend AbstractIntegrationTest
  - api-java/dify-api-boot/src/test/resources/application-test.yml # Remove hardcoded IPs
```

---

## Risks & Mitigations

| Risk                                        | Likelihood | Impact | Mitigation                                                      |
| ------------------------------------------- | ---------- | ------ | --------------------------------------------------------------- |
| Docker daemon not available on CI runner    | Medium     | High   | Document Docker requirement; most Java CI has Docker            |
| Flyway migration fails on clean TC database | Low        | Medium | Already uses `baseline-on-migrate: true`; test with clean DB    |
| Container startup slows test suite          | Low        | Low    | Static containers shared across all tests; ~5-10s one-time cost |
| Redis 6 vs 7 image mismatch                 | Low        | Low    | Use `redis:6-alpine` to match production Docker Compose exactly |

---

## Open Questions

| Question                                           | Owner | Due Date | Status   |
| -------------------------------------------------- | ----- | -------- | -------- |
| None — all key decisions resolved in handoff notes | —     | —        | Resolved |

**Resolved decisions from handoff:**

- Redis container: `GenericContainer("redis:6-alpine")` (simpler, fewer deps)
- Container lifecycle: static shared (matches existing manual cleanup pattern)
- Image versions: match production (`postgres:15-alpine`, `redis:6-alpine`)

---

## Tasks

### Add Testcontainers BOM to parent POM [build]

Testcontainers BOM is declared in `api-java/pom.xml` `<dependencyManagement>` so all child modules inherit consistent versions.

**Metadata:**

```yaml
depends_on: []
parallel: true
conflicts_with: ["Add Testcontainers test dependencies to boot module"]
files:
  - api-java/pom.xml
```

**Verification:**

- `grep -A2 'testcontainers-bom' api-java/pom.xml` shows BOM import
- `cd api-java && mvn validate` passes

### Add Testcontainers test dependencies to boot module [build]

`dify-api-boot/pom.xml` declares `org.testcontainers:postgresql` and `org.testcontainers:junit-jupiter` in test scope (versions inherited from BOM).

**Metadata:**

```yaml
depends_on: ["Add Testcontainers BOM to parent POM"]
parallel: false
conflicts_with: []
files:
  - api-java/dify-api-boot/pom.xml
```

**Verification:**

- `cd api-java && mvn dependency:tree -pl dify-api-boot | grep testcontainers` shows both artifacts
- `cd api-java && mvn compile -pl dify-api-boot` passes

### Create AbstractIntegrationTest base class [test-infra]

A shared `AbstractIntegrationTest` class exists with static `PostgreSQLContainer` (postgres:15-alpine) and `GenericContainer` (redis:6-alpine), `@DynamicPropertySource` injecting `spring.datasource.*` and `spring.data.redis.*` properties, `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@ActiveProfiles("test")`.

**Metadata:**

```yaml
depends_on: ["Add Testcontainers test dependencies to boot module"]
parallel: false
conflicts_with: []
files:
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/AbstractIntegrationTest.java
```

**Verification:**

- `javac` compilation succeeds via `cd api-java && mvn test-compile -pl dify-api-boot`
- Class contains `@Testcontainers`, `@Container`, `@DynamicPropertySource` annotations

### Remove hardcoded Docker IPs from test config [config]

`application-test.yml` no longer contains hardcoded `172.x.x.x` IPs in connection properties. Non-connection config (logging, JWT secret, Flyway settings, dify app config) is preserved.

**Metadata:**

```yaml
depends_on: ["Create AbstractIntegrationTest base class"]
parallel: true
conflicts_with: ["Refactor existing contract tests to extend base class"]
files:
  - api-java/dify-api-boot/src/test/resources/application-test.yml
```

**Verification:**

- `! grep -E '172\.\d+\.\d+\.\d+' api-java/dify-api-boot/src/test/resources/application-test.yml` exits 0
- File still contains `spring.flyway`, `dify.*`, and `logging.*` sections

### Refactor existing contract tests to extend base class [test]

Both `ConsoleBootstrapContractTest` and `ConsoleFeatureContractTest` extend `AbstractIntegrationTest`, removing their own `@SpringBootTest` and `@ActiveProfiles` annotations (inherited from base).

**Metadata:**

```yaml
depends_on:
  ["Create AbstractIntegrationTest base class", "Remove hardcoded Docker IPs from test config"]
parallel: false
conflicts_with: []
files:
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleBootstrapContractTest.java
  - api-java/dify-api-boot/src/test/java/com/dify/boot/contract/ConsoleFeatureContractTest.java
```

**Verification:**

- `grep 'extends AbstractIntegrationTest' api-java/dify-api-boot/src/test/java/com/dify/boot/contract/*ContractTest.java` matches both files
- `cd api-java && mvn test-compile -pl dify-api-boot` passes

### Run full test suite and verify [verification]

All contract tests pass with Testcontainers-managed containers on a clean environment (no pre-existing Docker network required).

**Metadata:**

```yaml
depends_on: ["Refactor existing contract tests to extend base class"]
parallel: false
conflicts_with: []
files: []
```

**Verification:**

- `cd api-java && mvn test -pl dify-api-boot` exits 0
- Test output shows Testcontainers startup logs (container image pulls / starts)

---

## Dependency Legend

| Field            | Purpose                                           | Example                                    |
| ---------------- | ------------------------------------------------- | ------------------------------------------ |
| `depends_on`     | Must complete before this task starts             | `["Setup database", "Create schema"]`      |
| `parallel`       | Can run concurrently with other parallel tasks    | `true` / `false`                           |
| `conflicts_with` | Cannot run in parallel (same files)               | `["Update config"]`                        |
| `files`          | Files this task modifies (for conflict detection) | `["src/db/schema.ts", "src/db/client.ts"]` |

---

## Notes

- Container images must match production: `postgres:15-alpine` and `redis:6-alpine` (from `docker/docker-compose.yaml`)
- Spring Boot 3.4.3 has native Testcontainers support via `spring-boot-testcontainers` — but the manual `@DynamicPropertySource` approach is simpler and sufficient here
- Flyway with `baseline-on-migrate: true` handles clean TC databases without issues
- Existing tests use `JdbcTemplate` DELETE for cleanup between tests — this pattern remains valid with shared containers
