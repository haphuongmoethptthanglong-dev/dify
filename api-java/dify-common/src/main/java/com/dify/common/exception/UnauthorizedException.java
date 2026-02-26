package com.dify.common.exception;

/**
 * 401 Unauthorized exception.
 *
 * Matches Python's unauthorized handler in ext_login.py:
 * {@code {"code": "unauthorized", "message": "Unauthorized."}} with status 401.
 */
public class UnauthorizedException extends DifyApiException {

    private static final String DEFAULT_ERROR_CODE = "unauthorized";
    private static final int HTTP_STATUS = 401;

    public UnauthorizedException() {
        super(DEFAULT_ERROR_CODE, "Unauthorized.", HTTP_STATUS);
    }

    public UnauthorizedException(String message) {
        super(DEFAULT_ERROR_CODE, message, HTTP_STATUS);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, HTTP_STATUS, cause);
    }
}
