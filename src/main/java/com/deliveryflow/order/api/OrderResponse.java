package com.deliveryflow.order.api;

import com.deliveryflow.order.domain.Order;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderNo,
        String recipientName,
        String recipientPhone,
        String address,
        LocalDate requestedDate,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNo(), order.getRecipientName(),
                order.getRecipientPhone(), order.getAddress(), order.getRequestedDate(), order.getCreatedAt());
    }
}
