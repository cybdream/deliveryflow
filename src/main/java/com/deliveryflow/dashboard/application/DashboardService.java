package com.deliveryflow.dashboard.application;

import com.deliveryflow.dashboard.api.DeliveryStatusSummaryResponse;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final DeliveryRepository deliveryRepository;
    public DashboardService(DeliveryRepository deliveryRepository) { this.deliveryRepository = deliveryRepository; }

    public DeliveryStatusSummaryResponse summarize(LocalDate scheduledDate) {
        Map<DeliveryStatus, Long> aggregated = new EnumMap<>(DeliveryStatus.class);
        for (DeliveryStatus status : DeliveryStatus.values()) aggregated.put(status, 0L);
        deliveryRepository.countByStatus(scheduledDate).forEach(row -> aggregated.put(row.getStatus(), row.getCount()));
        Map<String, Long> counts = new java.util.LinkedHashMap<>();
        aggregated.forEach((status, count) -> counts.put(status.name(), count));
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        return new DeliveryStatusSummaryResponse(scheduledDate, total, counts);
    }
}
