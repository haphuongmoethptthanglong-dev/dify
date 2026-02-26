package com.dify.common.exception;

public class BadRequestException extends DifyApiException {

    private static final int HTTP_STATUS = 400;

    public BadRequestException(String message) {
        super("bad_request", message, HTTP_STATUS);
    }

    public BadRequestException(String errorCode, String message) {
        super(errorCode, message, HTTP_STATUS);
    }
}
