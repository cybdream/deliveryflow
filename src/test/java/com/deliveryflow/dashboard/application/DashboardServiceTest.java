package com.deliveryflow.dashboard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.deliveryflow.dashboard.api.DeliveryStatusSummaryResponse;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DashboardServiceTest {
    private final DeliveryRepository repository = Mockito.mock(DeliveryRepository.class);
    private final DashboardService service = new DashboardService(repository);

    @Test
    void returnsAllStatusesIncludingZeroCountStatuses() {
        DeliveryRepository.DeliveryStatusCount inDelivery = new DeliveryRepository.DeliveryStatusCount() {
            public DeliveryStatus getStatus() { return DeliveryStatus.IN_DELIVERY; }
            public long getCount() { return 2L; }
        };
        when(repository.countByScheduledDateAndStatus(LocalDate.of(2026, 8, 13))).thenReturn(List.of(inDelivery));

        DeliveryStatusSummaryResponse response = service.summarize(LocalDate.of(2026, 8, 13));

        assertThat(response.totalCount()).isEqualTo(2L);
        assertThat(response.statusCounts()).containsEntry("IN_DELIVERY", 2L).containsEntry("DELIVERED", 0L);
    }
}

