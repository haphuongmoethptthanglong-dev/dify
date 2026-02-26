package com.dify.common.constants;

/**
 * HTTP header name constants used across the Dify API.
 *
 * Matches Python constants from {@code constants/__init__.py}.
 */
public final class DifyHeaders {

    private DifyHeaders() {
    }

    // Custom request headers
    public static final String X_APP_CODE = "X-App-Code";
    public static final String X_PASSPORT = "X-App-Passport";
    public static final String X_CSRF_TOKEN = "X-CSRF-Token";
    public static final String X_WORKSPACE_ID = "X-WORKSPACE-ID";

    // Exposed response headers
    public static final String X_VERSION = "X-Version";
    public static final String X_ENV = "X-Env";
    public static final String X_TRACE_ID = "X-Trace-Id";
}
