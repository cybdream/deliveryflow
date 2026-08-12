package com.deliveryflow.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNo(String orderNo);

    Page<Order> findByRecipientNameContainingIgnoreCaseOrOrderNoContainingIgnoreCase(
            String recipientName, String orderNo, Pageable pageable);
}
