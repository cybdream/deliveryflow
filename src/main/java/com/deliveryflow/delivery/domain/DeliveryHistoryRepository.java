package com.deliveryflow.delivery.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {
    List<DeliveryHistory> findByDeliveryIdOrderByChangedAtAsc(Long deliveryId);
}
