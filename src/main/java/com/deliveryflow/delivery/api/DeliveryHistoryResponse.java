package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.domain.DeliveryHistory;
import java.time.LocalDateTime;

public record DeliveryHistoryResponse(Long id, String actionType, String previousStatus, String currentStatus, String changedBy, String reason, LocalDateTime changedAt) {
    public static DeliveryHistoryResponse from(DeliveryHistory history) {
        return new DeliveryHistoryResponse(history.getId(), history.getActionType(), history.getPreviousStatus() == null ? null : history.getPreviousStatus().name(), history.getCurrentStatus().name(), history.getChangedBy(), history.getReason(), history.getChangedAt());
    }
}
