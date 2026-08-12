package com.deliveryflow.user.api;

import com.deliveryflow.user.application.DriverService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse response = driverService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/drivers/" + response.id())).body(response);
    }

    @GetMapping
    public List<DriverResponse> findAllActive() {
        return driverService.findAllActive();
    }
}
