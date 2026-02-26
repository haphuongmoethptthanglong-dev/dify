package com.dify.gateway.controller.console;

import com.dify.gateway.config.DifyProperties;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * Version check endpoint.
 *
 * Matches Python's {@code controllers/console/version.py}.
 * Unauthenticated — used by frontend to check for available updates
 * and retrieve feature flags.
 */
@RestController
@RequestMapping("/console/api")
public class VersionController {

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    private final DifyProperties difyProperties;
    private final RestClient restClient;

    public VersionController(DifyProperties difyProperties) {
        this.difyProperties = difyProperties;
        this.restClient = RestClient.builder()
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @GetMapping("/version")
    public VersionResponse checkVersion(
            @RequestParam("current_version") String currentVersion) {

        var result = new VersionResponse();
        result.version = difyProperties.getVersion();
        result.releaseDate = "";
        result.releaseNotes = "";
        result.canAutoUpdate = false;
        result.features = new VersionFeatures(
                difyProperties.getFeatures().isCanReplaceLogo(),
                difyProperties.getFeatures().isModelLoadBalancingEnabled());

        String checkUpdateUrl = difyProperties.getCheckUpdateUrl();
        if (checkUpdateUrl == null || checkUpdateUrl.isBlank()) {
            return result;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> content = restClient.get()
                    .uri(checkUpdateUrl + "?current_version={version}", currentVersion)
                    .retrieve()
                    .body(Map.class);

            if (content == null) {
                result.version = currentVersion;
                return result;
            }

            String latestVersion = (String) content.getOrDefault("version", result.version);
            if (hasNewVersion(latestVersion, currentVersion)) {
                result.version = latestVersion;
                result.releaseDate = (String) content.getOrDefault("releaseDate", "");
                result.releaseNotes = (String) content.getOrDefault("releaseNotes", "");
                result.canAutoUpdate = Boolean.TRUE.equals(content.get("canAutoUpdate"));
            }
        } catch (Exception e) {
            logger.warn("Check update version error: {}.", e.getMessage());
            result.version = currentVersion;
        }

        return result;
    }

    /**
     * Compare semantic versions.
     * Matches Python's {@code _has_new_version()} using packaging.version.
     */
    static boolean hasNewVersion(String latestVersion, String currentVersion) {
        try {
            int[] latest = parseVersion(latestVersion);
            int[] current = parseVersion(currentVersion);
            for (int i = 0; i < Math.max(latest.length, current.length); i++) {
                int l = i < latest.length ? latest[i] : 0;
                int c = i < current.length ? current[i] : 0;
                if (l > c) return true;
                if (l < c) return false;
            }
            return false;
        } catch (Exception e) {
            logger.warn("Invalid version format: latest={}, current={}", latestVersion, currentVersion);
            return false;
        }
    }

    private static int[] parseVersion(String version) {
        // Strip leading 'v' if present, strip pre-release suffix
        String cleaned = version.startsWith("v") ? version.substring(1) : version;
        int dashIdx = cleaned.indexOf('-');
        if (dashIdx > 0) {
            cleaned = cleaned.substring(0, dashIdx);
        }
        String[] parts = cleaned.split("\\.");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    public static class VersionResponse {
        public String version;
        public String releaseDate;
        public String releaseNotes;
        public boolean canAutoUpdate;
        public VersionFeatures features;
    }

    public record VersionFeatures(boolean canReplaceLogo, boolean modelLoadBalancingEnabled) {
    }
}
