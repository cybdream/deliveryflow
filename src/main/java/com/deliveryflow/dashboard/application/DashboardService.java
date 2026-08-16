package com.deliveryflow.dashboard.application;

import com.deliveryflow.dashboard.api.DeliveryStatusSummaryResponse;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates delivery status summaries for administrator monitoring screens.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final DeliveryRepository deliveryRepository;

    public DashboardService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Returns every status with a count, including statuses whose count is zero.
     */
    public DeliveryStatusSummaryResponse summarize(LocalDate scheduledDate) {
        Map<DeliveryStatus, Long> aggregated = initializeStatusCounts();

        // Use the date-specific query only when the caller supplied a scheduled date.
        var rows = scheduledDate == null
                ? deliveryRepository.countAllByStatus()
                : deliveryRepository.countByScheduledDateAndStatus(scheduledDate);
        rows.forEach(row -> aggregated.put(row.getStatus(), row.getCount()));

        Map<String, Long> counts = new LinkedHashMap<>();
        aggregated.forEach((status, count) -> counts.put(status.name(), count));

        long totalCount = counts.values().stream().mapToLong(Long::longValue).sum();
        return new DeliveryStatusSummaryResponse(scheduledDate, totalCount, counts);
    }

    private Map<DeliveryStatus, Long> initializeStatusCounts() {
        Map<DeliveryStatus, Long> counts = new EnumMap<>(DeliveryStatus.class);
        for (DeliveryStatus status : DeliveryStatus.values()) {
            counts.put(status, 0L);
        }

        return counts;
    }
}