package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.application.DeliveryService;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes administrator and driver delivery operations over HTTP.
 */
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Assigns an order to a driver and returns the newly created delivery.
     */
    @PostMapping
    public ResponseEntity<DeliveryResponse> assign(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.assign(request);
        URI location = URI.create("/api/v1/deliveries/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Returns deliveries for administrators with optional status, driver, and date filters.
     */
    @GetMapping
    public Page<DeliveryResponse> findAll(@RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) LocalDate scheduledDate,
            @ParameterObject @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable) {
        return deliveryService.findAll(status, driverId, scheduledDate, pageable);
    }

    /**
     * Returns deliveries assigned to the currently logged-in driver.
     */
    @GetMapping("/me")
    public Page<DeliveryResponse> findMine(Authentication authentication,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) LocalDate scheduledDate,
            @ParameterObject @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable) {
        return deliveryService.findMine(authentication.getName(), status, scheduledDate, pageable);
    }

    /**
     * Changes a delivery status after applying the caller's role permissions.
     */
    @PatchMapping("/{deliveryId}/status")
    public DeliveryResponse updateStatus(@PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        UpdateDeliveryStatusRequest request = new UpdateDeliveryStatusRequest(status, reason);

        return deliveryService.updateStatus(deliveryId, request, authentication.getName(), isAdmin(authentication));
    }

    /**
     * Returns the audit history for one delivery.
     */
    @GetMapping("/{deliveryId}/histories")
    public List<DeliveryHistoryResponse> findHistories(@PathVariable Long deliveryId,
            Authentication authentication) {
        return deliveryService.findHistories(deliveryId, authentication.getName(), isAdmin(authentication));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}