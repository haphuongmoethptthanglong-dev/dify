package com.dify.common.exception;

public class NotFoundException extends DifyApiException {

    private static final int HTTP_STATUS = 404;

    public NotFoundException(String message) {
        super("not_found", message, HTTP_STATUS);
    }

    public NotFoundException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }
}
