package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.application.DeliveryService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PatchMapping("/{deliveryId}/status")
    public DeliveryResponse updateStatus(@PathVariable Long deliveryId, @Valid @RequestBody UpdateDeliveryStatusRequest request, Authentication authentication) {
        return deliveryService.updateStatus(deliveryId, request, authentication.getName(), isAdmin(authentication));
    }
    @GetMapping("/{deliveryId}/histories")
    public List<DeliveryHistoryResponse> findHistories(@PathVariable Long deliveryId, Authentication authentication) {
        return deliveryService.findHistories(deliveryId, authentication.getName(), isAdmin(authentication));
    }
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);
    }
}
