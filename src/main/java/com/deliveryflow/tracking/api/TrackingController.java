package com.deliveryflow.tracking.api;

import com.deliveryflow.tracking.application.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer Tracking", description = "Public delivery tracking with order or shipment number and recipient phone verification")
@Validated
@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Operation(summary = "Track by order number", description = "Returns limited delivery information when the order number and recipient phone both match.")
    @GetMapping("/orders/{orderNo}")
    public TrackingResponse trackByOrderNo(
            @Parameter(description = "Order number", example = "ORD-20260816-8503")
            @PathVariable @NotBlank @Size(max = 21) String orderNo,
            @Parameter(description = "Recipient phone number", example = "010-1234-5678")
            @RequestParam @NotBlank @Pattern(regexp = "^[0-9-]{10,20}$") String recipientPhone
    ) {
        return trackingService.trackByOrderNo(orderNo, recipientPhone);
    }

    @Operation(summary = "Track by shipment number", description = "Returns limited delivery information when the shipment number and recipient phone both match.")
    @GetMapping("/shipments/{trackingNo}")
    public TrackingResponse trackByTrackingNo(
            @Parameter(description = "Internal shipment tracking number", example = "TRK-20260816-12345678")
            @PathVariable @NotBlank @Size(max = 30) String trackingNo,
            @Parameter(description = "Recipient phone number", example = "010-1234-5678")
            @RequestParam @NotBlank @Pattern(regexp = "^[0-9-]{10,20}$") String recipientPhone
    ) {
        return trackingService.trackByTrackingNo(trackingNo, recipientPhone);
    }
}
