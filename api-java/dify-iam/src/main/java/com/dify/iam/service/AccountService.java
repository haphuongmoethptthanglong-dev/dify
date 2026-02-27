package com.dify.iam.service;

import com.dify.common.exception.UnauthorizedException;
import com.dify.iam.entity.Account;
import com.dify.iam.entity.Tenant;
import com.dify.iam.entity.TenantAccountJoin;
import com.dify.iam.entity.TenantAccountRole;
import com.dify.iam.repository.AccountRepository;
import com.dify.iam.repository.TenantAccountJoinRepository;
import com.dify.iam.repository.TenantRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for resolving Account + Tenant context from JWT user_id.
 *
 * Matches Python's {@code AccountService.load_logged_in_account()} in
 * {@code services/account_service.py}.
 */
@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final TenantAccountJoinRepository tenantAccountJoinRepository;
    private final TenantRepository tenantRepository;

    public AccountService(AccountRepository accountRepository,
                          TenantAccountJoinRepository tenantAccountJoinRepository,
                          TenantRepository tenantRepository) {
        this.accountRepository = accountRepository;
        this.tenantAccountJoinRepository = tenantAccountJoinRepository;
        this.tenantRepository = tenantRepository;
    }

    /**
     * Load an account by ID and resolve its current tenant context.
     * Matches Python's {@code AccountService.load_logged_in_account()}.
     *
     * <p>Steps:
     * <ol>
     *   <li>Find account by ID</li>
     *   <li>Find the current tenant (via TenantAccountJoin where current=true)</li>
     *   <li>Set the tenant and role on the Account object</li>
     * </ol>
     *
     * @param accountIdStr the account ID as a string (from JWT user_id claim)
     * @return the account with tenant context populated
     * @throws UnauthorizedException if account not found, banned, closed, or has no tenant
     */
    public Account loadLoggedInAccount(String accountIdStr) {
        UUID accountId;
        try {
            accountId = UUID.fromString(accountIdStr);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid account ID format");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UnauthorizedException("Account not found"));

        String status = account.getStatus();
        if ("banned".equals(status) || "closed".equals(status)) {
            throw new UnauthorizedException("Account is " + status);
        }

        TenantAccountJoin join = tenantAccountJoinRepository.findByAccountIdAndCurrentTrue(accountId)
                .orElse(null);

        if (join == null) {
            List<TenantAccountJoin> joins = tenantAccountJoinRepository.findByAccountId(accountId);
            if (!joins.isEmpty()) {
                join = joins.getFirst();
            }
        }

        if (join == null) {
            throw new UnauthorizedException("account_not_link_tenant");
        }

        Tenant tenant = tenantRepository.findById(join.getTenantId())
                .orElseThrow(() -> new UnauthorizedException("Tenant not found"));

        account.setCurrentTenant(tenant);
        account.setRole(TenantAccountRole.fromValue(join.getRole()));

        return account;
    }
}
