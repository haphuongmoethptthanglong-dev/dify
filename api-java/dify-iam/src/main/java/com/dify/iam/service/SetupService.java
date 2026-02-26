package com.dify.iam.service;

import com.dify.common.constants.DifyErrorCodes;
import com.dify.common.exception.DifyApiException;
import com.dify.iam.entity.Account;
import com.dify.iam.entity.DifySetup;
import com.dify.iam.entity.Tenant;
import com.dify.iam.entity.TenantAccountJoin;
import com.dify.iam.repository.AccountRepository;
import com.dify.iam.repository.DifySetupRepository;
import com.dify.iam.repository.TenantAccountJoinRepository;
import com.dify.iam.repository.TenantRepository;
import com.dify.iam.util.PasswordService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling first-time system setup and init password validation.
 *
 * Mirrors the combination of:
 * <ul>
 *   <li>{@code RegisterService.setup()} in services/account_service.py</li>
 *   <li>{@code TenantService.get_tenant_count()} in services/account_service.py</li>
 *   <li>{@code get_init_validate_status()} in controllers/console/init_validate.py</li>
 * </ul>
 *
 * Init validation state uses Redis with client IP as key (replaces Flask session).
 * The Redis key has a 30-minute TTL — enough time for the admin to complete setup
 * after validating the init password.
 */
@Service
public class SetupService {

    private static final Logger logger = LoggerFactory.getLogger(SetupService.class);
    private static final String INIT_VALIDATED_KEY_PREFIX = "init_validated:";
    private static final long INIT_VALIDATED_TTL_MINUTES = 30;

    private final DifySetupRepository difySetupRepository;
    private final AccountRepository accountRepository;
    private final TenantRepository tenantRepository;
    private final TenantAccountJoinRepository tenantAccountJoinRepository;
    private final StringRedisTemplate redisTemplate;

    public SetupService(
            DifySetupRepository difySetupRepository,
            AccountRepository accountRepository,
            TenantRepository tenantRepository,
            TenantAccountJoinRepository tenantAccountJoinRepository,
            StringRedisTemplate redisTemplate) {
        this.difySetupRepository = difySetupRepository;
        this.accountRepository = accountRepository;
        this.tenantRepository = tenantRepository;
        this.tenantAccountJoinRepository = tenantAccountJoinRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Check if the system has been set up by looking for any {@link DifySetup} record.
     *
     * @return the setup record if present, empty otherwise
     */
    public Optional<DifySetup> getSetupStatus() {
        return difySetupRepository.findAll().stream().findFirst();
    }

    /**
     * Get the total tenant count.
     * Matches Python's {@code TenantService.get_tenant_count()}.
     */
    public long getTenantCount() {
        return tenantRepository.count();
    }

    /**
     * Check init validation status for a given client IP.
     *
     * Matches Python's {@code get_init_validate_status()} which checks:
     * 1. Flask session for {@code is_init_validated}
     * 2. Falls back to checking if DifySetup exists in DB
     *
     * @param edition     the deployment edition (SELF_HOSTED, CLOUD, etc.)
     * @param initPassword the configured init password (null if not set)
     * @param clientIp    the client's IP address
     * @return true if init validation is satisfied
     */
    public boolean isInitValidated(String edition, String initPassword, String clientIp) {
        if (!"SELF_HOSTED".equals(edition)) {
            return true;
        }
        if (initPassword == null || initPassword.isBlank()) {
            return true;
        }
        // Check Redis for session-equivalent state
        String key = INIT_VALIDATED_KEY_PREFIX + clientIp;
        if ("true".equals(redisTemplate.opsForValue().get(key))) {
            return true;
        }
        // Fall back to checking if setup is already complete
        return difySetupRepository.count() > 0;
    }

    /**
     * Mark init validation as passed for a client IP.
     * Replaces Flask's {@code session["is_init_validated"] = True}.
     */
    public void markInitValidated(String clientIp) {
        String key = INIT_VALIDATED_KEY_PREFIX + clientIp;
        redisTemplate.opsForValue().set(key, "true", INIT_VALIDATED_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Mark init validation as failed for a client IP.
     * Replaces Flask's {@code session["is_init_validated"] = False}.
     */
    public void markInitFailed(String clientIp) {
        String key = INIT_VALIDATED_KEY_PREFIX + clientIp;
        redisTemplate.delete(key);
    }

    /**
     * Perform first-time system setup: create admin account, tenant, and setup record.
     *
     * Matches Python's {@code RegisterService.setup()} which:
     * 1. Creates the admin account with hashed password
     * 2. Creates an owner tenant
     * 3. Creates a TenantAccountJoin (owner role)
     * 4. Writes a DifySetup record
     * 5. On failure, cleans up all created records
     *
     * @param email    admin email (should be pre-lowercased)
     * @param name     admin display name
     * @param password admin password (already validated)
     * @param ipAddress client IP for last_login_ip
     * @param language interface language (nullable)
     * @param appVersion current app version for DifySetup record
     */
    @Transactional
    public void setup(String email, String name, String password, String ipAddress,
                      String language, String appVersion) {
        try {
            // Create admin account
            String salt = PasswordService.generateSalt();
            String hashedPassword = PasswordService.hashPassword(password, salt);
            LocalDateTime now = LocalDateTime.now();

            UUID accountId = UUID.randomUUID();
            Account account = new Account(accountId, name, email);
            account.setPassword(hashedPassword);
            account.setPasswordSalt(salt);
            account.setInterfaceLanguage(language != null ? language : "en-US");
            account.setInterfaceTheme("light");
            account.setTimezone(resolveTimezone(language));
            account.setStatus("active");
            account.setLastLoginIp(ipAddress);
            account.setLastActiveAt(now);
            account.setInitializedAt(now);
            accountRepository.save(account);

            // Create owner tenant
            UUID tenantId = UUID.randomUUID();
            Tenant tenant = new Tenant(tenantId, name + "'s Workspace");
            tenant.setStatus("normal");
            tenantRepository.save(tenant);

            // Link account to tenant as owner
            UUID joinId = UUID.randomUUID();
            TenantAccountJoin join = new TenantAccountJoin(joinId, tenantId, accountId, "owner");
            join.setCurrent(true);
            tenantAccountJoinRepository.save(join);

            // Record setup completion
            DifySetup setup = new DifySetup(appVersion);
            difySetupRepository.save(setup);

            logger.info("System setup completed: email={}, tenant={}", email, tenant.getId());
        } catch (Exception e) {
            logger.error("Setup failed for email={}, name={}", email, name, e);
            throw new DifyApiException(
                    DifyErrorCodes.INTERNAL_SERVER_ERROR,
                    "Setup failed: " + e.getMessage(),
                    500,
                    e);
        }
    }

    private static String resolveTimezone(String language) {
        if (language == null) {
            return "UTC";
        }
        return switch (language) {
            case "zh-Hans" -> "Asia/Shanghai";
            case "zh-Hant" -> "Asia/Taipei";
            case "ja-JP" -> "Asia/Tokyo";
            case "ko-KR" -> "Asia/Seoul";
            case "pt-BR" -> "America/Sao_Paulo";
            case "vi-VN" -> "Asia/Ho_Chi_Minh";
            case "uk-UA" -> "Europe/Kyiv";
            case "tr-TR" -> "Europe/Istanbul";
            default -> "UTC";
        };
    }
}
