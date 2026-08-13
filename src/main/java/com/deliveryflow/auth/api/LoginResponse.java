package com.deliveryflow.auth.api;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, String role, String name) { }
