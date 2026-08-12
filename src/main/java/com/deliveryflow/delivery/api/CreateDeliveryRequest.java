package com.deliveryflow.delivery.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateDeliveryRequest(
        @NotNull Long orderId,
        @NotNull Long driverId,
        @NotNull @FutureOrPresent LocalDate scheduledDate
) {
}
