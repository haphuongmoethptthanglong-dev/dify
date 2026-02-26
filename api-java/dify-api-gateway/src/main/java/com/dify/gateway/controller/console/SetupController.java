package com.dify.gateway.controller.console;

import com.dify.gateway.config.DifyProperties;
import com.dify.common.constants.DifyErrorCodes;
import com.dify.common.exception.DifyApiException;
import com.dify.iam.entity.DifySetup;
import com.dify.iam.service.SetupService;
import com.dify.iam.util.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * System setup controller for first-time installation.
 *
 * Matches Python's {@code controllers/console/setup.py}.
 * Unauthenticated by design — no admin account exists during first-time bootstrap.
 *
 * GET returns setup status; POST creates the admin account and tenant (SELF_HOSTED only).
 */
@RestController
@RequestMapping("/console/api")
public class SetupController {

    private final DifyProperties difyProperties;
    private final SetupService setupService;

    public SetupController(DifyProperties difyProperties, SetupService setupService) {
        this.difyProperties = difyProperties;
        this.setupService = setupService;
    }

    @GetMapping("/setup")
    public Map<String, Object> getSetupStatus() {
        if ("SELF_HOSTED".equals(difyProperties.getEdition())) {
            Optional<DifySetup> setup = setupService.getSetupStatus();
            if (setup.isPresent()) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("step", "finished");
                result.put("setup_at", setup.get().getSetupAt().toString());
                return result;
            }
            Map<String, Object> notStarted = new LinkedHashMap<>();
            notStarted.put("step", "not_started");
            notStarted.put("setup_at", null);
            return notStarted;
        }
        Map<String, Object> finished = new LinkedHashMap<>();
        finished.put("step", "finished");
        finished.put("setup_at", null);
        return finished;
    }

    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> setup(
            @RequestBody SetupPayload payload,
            HttpServletRequest request) {

        if (!"SELF_HOSTED".equals(difyProperties.getEdition())) {
            throw new DifyApiException(DifyErrorCodes.NOT_FOUND, "Not Found", 404);
        }

        if (setupService.getSetupStatus().isPresent()) {
            throw new DifyApiException(
                    DifyErrorCodes.ALREADY_SETUP,
                    "Dify has been successfully installed. "
                            + "Please refresh the page or return to the dashboard homepage.",
                    403);
        }

        if (setupService.getTenantCount() > 0) {
            throw new DifyApiException(
                    DifyErrorCodes.ALREADY_SETUP,
                    "Dify has been successfully installed. "
                            + "Please refresh the page or return to the dashboard homepage.",
                    403);
        }

        String clientIp = InitValidateController.extractClientIp(request);
        if (!setupService.isInitValidated(
                difyProperties.getEdition(),
                difyProperties.getInitPassword(),
                clientIp)) {
            throw new DifyApiException(
                    DifyErrorCodes.NOT_INIT_VALIDATED,
                    "Init validation has not been completed yet. "
                            + "Please proceed with the init validation process first.",
                    401);
        }

        // Validate payload
        if (payload.email() == null || payload.email().isBlank()) {
            throw new DifyApiException(DifyErrorCodes.BAD_REQUEST, "Email is required.", 400);
        }
        if (payload.name() == null || payload.name().isBlank()) {
            throw new DifyApiException(DifyErrorCodes.BAD_REQUEST, "Name is required.", 400);
        }
        if (payload.name().length() > 30) {
            throw new DifyApiException(DifyErrorCodes.BAD_REQUEST, "Name must be at most 30 characters.", 400);
        }
        if (payload.password() == null || payload.password().isBlank()) {
            throw new DifyApiException(DifyErrorCodes.BAD_REQUEST, "Password is required.", 400);
        }
        try {
            PasswordService.validPassword(payload.password());
        } catch (IllegalArgumentException e) {
            throw new DifyApiException(DifyErrorCodes.BAD_REQUEST, e.getMessage(), 400);
        }

        setupService.setup(
                payload.email().toLowerCase(),
                payload.name(),
                payload.password(),
                clientIp,
                payload.language(),
                difyProperties.getVersion());

        return Map.of("result", "success");
    }

    public record SetupPayload(String email, String name, String password, String language) {
    }
}
