package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.application.DeliveryService;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;
    public DeliveryController(DeliveryService deliveryService) { this.deliveryService = deliveryService; }
    @PostMapping
    public ResponseEntity<DeliveryResponse> assign(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.assign(request);
        return ResponseEntity.created(URI.create("/api/v1/deliveries/" + response.id())).body(response);
    }
    @GetMapping
    public Page<DeliveryResponse> findAll(@RequestParam(required = false) DeliveryStatus status, @RequestParam(required = false) Long driverId, @RequestParam(required = false) LocalDate scheduledDate, @ParameterObject @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable) {
        return deliveryService.findAll(status, driverId, scheduledDate, pageable);
    }
    @GetMapping("/me")
    public Page<DeliveryResponse> findMine(Authentication authentication, @RequestParam(required = false) DeliveryStatus status, @RequestParam(required = false) LocalDate scheduledDate, @ParameterObject @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable) {
        return deliveryService.findMine(authentication.getName(), status, scheduledDate, pageable);
    }
    @PatchMapping("/{deliveryId}/status")
    public DeliveryResponse updateStatus(@PathVariable Long deliveryId, @Valid @RequestBody UpdateDeliveryStatusRequest request, Authentication authentication) {
        return deliveryService.updateStatus(deliveryId, request, authentication.getName(), isAdmin(authentication));
    }
    @GetMapping("/{deliveryId}/histories")
    public List<DeliveryHistoryResponse> findHistories(@PathVariable Long deliveryId, Authentication authentication) {
        return deliveryService.findHistories(deliveryId, authentication.getName(), isAdmin(authentication));
    }
    private boolean isAdmin(Authentication authentication) { return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals); }
}

