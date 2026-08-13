package com.deliveryflow.dashboard.api;

import com.deliveryflow.dashboard.application.DashboardService;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }

    @GetMapping("/delivery-status")
    public DeliveryStatusSummaryResponse deliveryStatus(@RequestParam(required = false) LocalDate scheduledDate) {
        return dashboardService.summarize(scheduledDate);
    }
}
