package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.domain.Delivery;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        Long orderId,
        String orderNo,
        Long driverId,
        String driverName,
        LocalDate scheduledDate,
        String status,
        LocalDateTime assignedAt
) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(delivery.getId(), delivery.getOrder().getId(), delivery.getOrder().getOrderNo(),
                delivery.getDriver().getId(), delivery.getDriver().getName(), delivery.getScheduledDate(),
                delivery.getStatus().name(), delivery.getAssignedAt());
    }
}
