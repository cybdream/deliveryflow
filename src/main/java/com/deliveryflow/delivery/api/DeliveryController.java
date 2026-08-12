package com.deliveryflow.delivery.api;

import com.deliveryflow.delivery.application.DeliveryService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<DeliveryResponse> assign(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.assign(request);
        return ResponseEntity.created(URI.create("/api/v1/deliveries/" + response.id())).body(response);
    }
}
