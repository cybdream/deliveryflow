package com.deliveryflow.common.api;

import com.deliveryflow.delivery.domain.DeliveryStatusTransitionException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts validation and domain exceptions into the common localized API error format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ApiMessageService messages;

    public GlobalExceptionHandler(ApiMessageService messages) {
        this.messages = messages;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                ApiErrorResponse.validation(request.getRequestURI(), messages.get("error.validation"), errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "MALFORMED_REQUEST",
                messages.get("error.malformedRequest"), request.getRequestURI()));
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        String message = messages.get(exception.getMessageKey(), exception.getMessageArguments());
        return ResponseEntity.status(exception.getStatus()).body(ApiErrorResponse.of(exception.getStatus().value(),
                exception.getCode(), message, request.getRequestURI()));
    }

    @ExceptionHandler(DeliveryStatusTransitionException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidTransition(DeliveryStatusTransitionException exception,
            HttpServletRequest request) {
        String message = messages.get("error.delivery.invalidTransition", exception.getPreviousStatus(),
                exception.getNextStatus());
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "BUSINESS_RULE_VIOLATION", message,
                request.getRequestURI()));
    }

    // Keep a safe localized response for any legacy IllegalArgumentException not migrated yet.
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "BUSINESS_RULE_VIOLATION",
                messages.get("error.businessRule"), request.getRequestURI()));
    }
}