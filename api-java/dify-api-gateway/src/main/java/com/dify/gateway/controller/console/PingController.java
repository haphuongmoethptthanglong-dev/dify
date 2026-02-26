package com.dify.gateway.controller.console;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for connection testing.
 *
 * Matches Python's {@code controllers/console/ping.py}.
 * Unauthenticated by design — used by frontend to verify API reachability.
 */
@RestController
@RequestMapping("/console/api")
public class PingController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("result", "pong");
    }
}
