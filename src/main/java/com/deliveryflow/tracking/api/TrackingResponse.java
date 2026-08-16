package com.deliveryflow.tracking.api;

import com.deliveryflow.delivery.domain.Delivery;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrackingResponse(
        String orderNo,
        String trackingNo,
        String status,
        LocalDate scheduledDate,
        LocalDateTime deliveredAt
) {
    public static TrackingResponse from(Delivery delivery) {
        return new TrackingResponse(delivery.getOrder().getOrderNo(), delivery.getTrackingNo(), delivery.getStatus().name(),
                delivery.getScheduledDate(), delivery.getDeliveredAt());
    }
}
