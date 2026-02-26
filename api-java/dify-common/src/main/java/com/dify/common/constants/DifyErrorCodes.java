package com.dify.common.constants;

/**
 * Well-known error code strings used in the Dify API error envelope.
 */
public final class DifyErrorCodes {

    private DifyErrorCodes() {
    }

    public static final String UNAUTHORIZED = "unauthorized";
    public static final String FORBIDDEN = "forbidden";
    public static final String NOT_FOUND = "not_found";
    public static final String BAD_REQUEST = "bad_request";
    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
    public static final String UNKNOWN = "unknown";
}
