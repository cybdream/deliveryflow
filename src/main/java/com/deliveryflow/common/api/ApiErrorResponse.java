package com.deliveryflow.common.api;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(String timestamp, int status, String code, String message, String path, Map<String, String> fieldErrors) {
    public static ApiErrorResponse of(int status, String code, String message, String path) {
        return new ApiErrorResponse(LocalDateTime.now().toString(), status, code, message, path, Map.of());
    }
    public static ApiErrorResponse validation(String path, String message, Map<String, String> fieldErrors) {
        return new ApiErrorResponse(LocalDateTime.now().toString(), 400, "VALIDATION_FAILED", message, path, fieldErrors);
    }
}
