package com.deliveryflow.order.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateOrderRequest(
        @NotBlank @Size(max = 50) String recipientName,
        @NotBlank @Pattern(regexp = "^[0-9-]{10,20}$", message = "{validation.order.recipientPhone}") String recipientPhone,
        @NotBlank @Size(max = 300) String address,
        @NotNull @FutureOrPresent LocalDate requestedDate
) {
}
