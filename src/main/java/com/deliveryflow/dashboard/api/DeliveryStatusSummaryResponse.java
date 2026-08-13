package com.deliveryflow.dashboard.api;

import java.time.LocalDate;
import java.util.Map;

public record DeliveryStatusSummaryResponse(LocalDate scheduledDate, long totalCount, Map<String, Long> statusCounts) { }
