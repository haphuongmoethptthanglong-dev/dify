package com.dify.common.exception;

/**
 * Base exception for all Dify API errors.
 *
 * Maps to Python's {@code BaseHTTPException} which carries an error_code string,
 * human-readable description, and HTTP status code.
 *
 * Subclasses should set meaningful {@code errorCode} and {@code httpStatus} values.
 * The {@link GlobalExceptionHandler} in dify-api-gateway converts these to the
 * standard {@link ErrorResponse} JSON envelope.
 */
public class DifyApiException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public DifyApiException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public DifyApiException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(errorCode, getMessage(), httpStatus);
    }
}
