package com.dify.common.exception;

public class ForbiddenException extends DifyApiException {

    private static final int HTTP_STATUS = 403;

    public ForbiddenException(String message) {
        super("forbidden", message, HTTP_STATUS);
    }

    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }
}
