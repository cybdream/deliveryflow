package com.deliveryflow.common.api;

import org.springframework.http.HttpStatus;

/**
 * Represents an API error using a locale-independent message key.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String messageKey;
    private final Object[] messageArguments;

    private ApiException(HttpStatus status, String code, String messageKey, Object... messageArguments) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.messageArguments = messageArguments.clone();
    }

    public static ApiException businessRule(String messageKey, Object... messageArguments) {
        return new ApiException(HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", messageKey, messageArguments);
    }

    public static ApiException unauthorized(String messageKey) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", messageKey);
    }

    public static ApiException forbidden(String messageKey) {
        return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", messageKey);
    }

    public static ApiException notFound(String messageKey) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", messageKey);
    }

    public static ApiException internal(String messageKey) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", messageKey);
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessageKey() { return messageKey; }
    public Object[] getMessageArguments() { return messageArguments.clone(); }
}