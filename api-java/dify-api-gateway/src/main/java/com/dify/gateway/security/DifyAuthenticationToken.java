package com.dify.gateway.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Spring Security authentication token for Dify console users.
 *
 * Holds the account ID extracted from the JWT token and the resolved
 * account/tenant context after authentication.
 */
public class DifyAuthenticationToken extends AbstractAuthenticationToken {

    private final String accountId;
    private final String tenantId;

    public DifyAuthenticationToken(String accountId) {
        super(List.of());
        this.accountId = accountId;
        this.tenantId = null;
        setAuthenticated(false);
    }

    public DifyAuthenticationToken(String accountId, String tenantId,
                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.accountId = accountId;
        this.tenantId = tenantId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
