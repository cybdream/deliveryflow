package com.deliveryflow.common.api;

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
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ApiMessageService messages;
    public GlobalExceptionHandler(ApiMessageService messages) { this.messages = messages; }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        return ResponseEntity.badRequest().body(ApiErrorResponse.validation(request.getRequestURI(), messages.get("error.validation"), errors));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "MALFORMED_REQUEST", messages.get("error.malformedRequest"), request.getRequestURI()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(400, "BUSINESS_RULE_VIOLATION", messages.businessRule(exception.getMessage()), request.getRequestURI()));
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiErrorResponse> handleStatus(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() == null ? status.getReasonPhrase() : messages.businessRule(exception.getReason());
        return ResponseEntity.status(status).body(ApiErrorResponse.of(status.value(), status.name(), message, request.getRequestURI()));
    }
}
