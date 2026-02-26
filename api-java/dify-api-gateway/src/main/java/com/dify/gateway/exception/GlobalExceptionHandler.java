package com.dify.gateway.exception;

import com.dify.common.exception.DifyApiException;
import com.dify.common.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler producing the Dify error envelope.
 *
 * Matches Python's BaseHTTPException behavior:
 * {@code {"code": error_code, "message": description, "status": http_code}}
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DifyApiException.class)
    public ResponseEntity<ErrorResponse> handleDifyApiException(DifyApiException ex) {
        if (ex.getHttpStatus() >= 500) {
            logger.error("Server error: {}", ex.getMessage(), ex);
        }
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ex.toErrorResponse());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        var error = new ErrorResponse("not_found", "The requested resource was not found.", 404);
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        var error = new ErrorResponse("bad_request", ex.getMessage(), 400);
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestBody(HttpMessageNotReadableException ex) {
        var error = new ErrorResponse("bad_request", "Invalid request body.", 400);
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);
        var error = new ErrorResponse("internal_server_error", "Internal Server Error", 500);
        return ResponseEntity.status(500).body(error);
    }
}
