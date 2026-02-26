package com.dify.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard error response body matching the Python Dify API error envelope.
 *
 * Python source: {@code libs/exception.py} — BaseHTTPException produces:
 * {@code {"code": self.error_code, "message": self.description, "status": self.code}}
 */
public record ErrorResponse(
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("status") int status
) {
}
