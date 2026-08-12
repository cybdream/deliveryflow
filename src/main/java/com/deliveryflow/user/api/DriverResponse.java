package com.deliveryflow.user.api;

import com.deliveryflow.user.domain.User;
import java.time.LocalDateTime;

public record DriverResponse(
        Long id,
        String name,
        String email,
        String role,
        boolean active,
        LocalDateTime createdAt
) {
    public static DriverResponse from(User user) {
        return new DriverResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole().name(), user.isActive(), user.getCreatedAt());
    }
}
