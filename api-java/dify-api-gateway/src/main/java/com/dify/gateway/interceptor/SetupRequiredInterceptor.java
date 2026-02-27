package com.dify.gateway.interceptor;

import com.dify.common.constants.DifyErrorCodes;
import com.dify.common.exception.ErrorResponse;
import com.dify.gateway.config.DifyProperties;
import com.dify.iam.service.SetupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that mirrors Python's {@code @setup_required} decorator in
 * {@code api/controllers/console/wraps.py}.
 *
 * <p>For SELF_HOSTED editions, ensures the system has been initialized before
 * allowing requests through. Two error branches:
 * <ul>
 *   <li>initPassword configured but no DifySetup record → {@code not_init_validated} (401)</li>
 *   <li>No initPassword and no DifySetup record → {@code not_setup} (401)</li>
 * </ul>
 *
 * <p>Caches the setup-complete status in memory to avoid a DB query on every request.
 * Once setup is detected, the flag is never reset (setup cannot be undone).
 */
@Component
public class SetupRequiredInterceptor implements HandlerInterceptor {

    private final DifyProperties difyProperties;
    private final SetupService setupService;
    private final ObjectMapper objectMapper;

    private volatile boolean setupComplete;

    public SetupRequiredInterceptor(DifyProperties difyProperties,
                                    SetupService setupService,
                                    ObjectMapper objectMapper) {
        this.difyProperties = difyProperties;
        this.setupService = setupService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!"SELF_HOSTED".equals(difyProperties.getEdition())) {
            return true;
        }

        if (setupComplete) {
            return true;
        }

        boolean hasSetup = setupService.getSetupStatus().isPresent();
        if (hasSetup) {
            setupComplete = true;
            return true;
        }

        String initPassword = difyProperties.getInitPassword();
        if (initPassword != null && !initPassword.isBlank()) {
            writeErrorResponse(response,
                    DifyErrorCodes.NOT_INIT_VALIDATED,
                    "Init validation has not been completed yet. "
                            + "Please proceed with the init validation process first.",
                    HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            writeErrorResponse(response,
                    DifyErrorCodes.NOT_SETUP,
                    "Dify has not been initialized and installed yet. "
                            + "Please proceed with the initialization and installation process first.",
                    HttpServletResponse.SC_UNAUTHORIZED);
        }
        return false;
    }

    private void writeErrorResponse(HttpServletResponse response, String code, String message,
                                    int status) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(code, message, status));
    }
}
