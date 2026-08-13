package com.deliveryflow.delivery.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {
    boolean existsByOrderId(Long orderId);

    @Query("select d.status as status, count(d) as count from Delivery d group by d.status")
    List<DeliveryStatusCount> countAllByStatus();

    @Query("select d.status as status, count(d) as count from Delivery d where d.scheduledDate = :scheduledDate group by d.status")
    List<DeliveryStatusCount> countByScheduledDateAndStatus(@Param("scheduledDate") LocalDate scheduledDate);

    interface DeliveryStatusCount { DeliveryStatus getStatus(); long getCount(); }
}
