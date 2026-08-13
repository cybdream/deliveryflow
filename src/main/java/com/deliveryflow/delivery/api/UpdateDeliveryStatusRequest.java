package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.domain.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDeliveryStatusRequest(
        @NotNull DeliveryStatus status,
        @Size(max = 300) String reason,
        @NotBlank @Size(max = 50) String changedBy
) { }
