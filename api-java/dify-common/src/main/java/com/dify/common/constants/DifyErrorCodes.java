package com.dify.common.constants;

/**
 * Well-known error code strings used in the Dify API error envelope.
 */
public final class DifyErrorCodes {

    private DifyErrorCodes() {
    }

    // Generic error codes
    public static final String UNAUTHORIZED = "unauthorized";
    public static final String FORBIDDEN = "forbidden";
    public static final String NOT_FOUND = "not_found";
    public static final String BAD_REQUEST = "bad_request";
    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
    public static final String UNKNOWN = "unknown";

    // Console bootstrap error codes (controllers/console/error.py)
    public static final String ALREADY_SETUP = "already_setup";
    public static final String NOT_SETUP = "not_setup";
    public static final String NOT_INIT_VALIDATED = "not_init_validated";
    public static final String INIT_VALIDATE_FAILED = "init_validate_failed";
    public static final String ACCOUNT_NOT_INITIALIZED = "account_not_initialized";
}
