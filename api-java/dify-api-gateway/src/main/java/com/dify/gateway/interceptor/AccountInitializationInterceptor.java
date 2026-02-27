package com.dify.gateway.interceptor;

import com.dify.common.constants.DifyErrorCodes;
import com.dify.common.exception.ErrorResponse;
import com.dify.gateway.security.DifyAuthenticationToken;
import com.dify.iam.entity.Account;
import com.dify.iam.entity.AccountStatus;
import com.dify.iam.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that mirrors Python's {@code @account_initialization_required} decorator in
 * {@code api/controllers/console/wraps.py}.
 *
 * <p>Also serves as the "account loader": resolves the JWT userId into a full
 * {@link Account} (with tenant context) and stores it as request attribute
 * {@value #CURRENT_ACCOUNT_ATTR} so controllers can retrieve it without
 * repeating the lookup.
 *
 * <p>If the account status is {@link AccountStatus#UNINITIALIZED}, the request
 * is rejected with a 400 error.
 */
@Component
public class AccountInitializationInterceptor implements HandlerInterceptor {

    public static final String CURRENT_ACCOUNT_ATTR = "currentAccount";

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    public AccountInitializationInterceptor(AccountService accountService,
                                            ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof DifyAuthenticationToken token) || !token.isAuthenticated()) {
            return true;
        }

        Account account = accountService.loadLoggedInAccount(token.getAccountId());
        request.setAttribute(CURRENT_ACCOUNT_ATTR, account);

        if (account.getAccountStatus() == AccountStatus.UNINITIALIZED) {
            writeErrorResponse(response,
                    DifyErrorCodes.ACCOUNT_NOT_INITIALIZED,
                    "The account has not been initialized yet. "
                            + "Please proceed with the initialization process first.",
                    HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }

    private void writeErrorResponse(HttpServletResponse response, String code, String message,
                                    int status) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(code, message, status));
    }
}
