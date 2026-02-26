package com.dify.gateway.controller.console;

import com.dify.gateway.config.DifyProperties;
import com.dify.common.constants.DifyErrorCodes;
import com.dify.common.exception.DifyApiException;
import com.dify.iam.service.SetupService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Init password validation controller.
 *
 * Matches Python's {@code controllers/console/init_validate.py}.
 * Unauthenticated — used during first-time setup when INIT_PASSWORD is configured.
 *
 * Python uses Flask {@code session} to store {@code is_init_validated}; Java
 * replaces this with a Redis key scoped by client IP (stateless sessions).
 */
@RestController
@RequestMapping("/console/api")
public class InitValidateController {

    private final DifyProperties difyProperties;
    private final SetupService setupService;

    public InitValidateController(DifyProperties difyProperties, SetupService setupService) {
        this.difyProperties = difyProperties;
        this.setupService = setupService;
    }

    @GetMapping("/init")
    public Map<String, String> getInitStatus(HttpServletRequest request) {
        boolean validated = setupService.isInitValidated(
                difyProperties.getEdition(),
                difyProperties.getInitPassword(),
                extractClientIp(request));
        return Map.of("status", validated ? "finished" : "not_started");
    }

    @PostMapping("/init")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> validateInitPassword(
            @RequestBody InitValidatePayload payload,
            HttpServletRequest request) {

        if (!"SELF_HOSTED".equals(difyProperties.getEdition())) {
            throw new DifyApiException(DifyErrorCodes.NOT_FOUND, "Not Found", 404);
        }

        if (setupService.getTenantCount() > 0) {
            throw new DifyApiException(
                    DifyErrorCodes.ALREADY_SETUP,
                    "Dify has been successfully installed. "
                            + "Please refresh the page or return to the dashboard homepage.",
                    403);
        }

        String initPassword = difyProperties.getInitPassword();
        if (initPassword == null || !initPassword.equals(payload.password())) {
            setupService.markInitFailed(extractClientIp(request));
            throw new DifyApiException(
                    DifyErrorCodes.INIT_VALIDATE_FAILED,
                    "Init validation failed. Please check the password and try again.",
                    401);
        }

        setupService.markInitValidated(extractClientIp(request));
        return Map.of("result", "success");
    }

    static String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    public record InitValidatePayload(String password) {
    }
}
