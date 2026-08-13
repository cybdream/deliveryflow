package com.deliveryflow.delivery.domain;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public final class DeliverySpecifications {
    private DeliverySpecifications() { }

    public static Specification<Delivery> hasStatus(DeliveryStatus status) {
        return status == null ? null : (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    public static Specification<Delivery> hasDriverId(Long driverId) {
        return driverId == null ? null : (root, query, builder) -> builder.equal(root.get("driver").get("id"), driverId);
    }

    public static Specification<Delivery> hasDriverEmail(String email) {
        return (root, query, builder) -> builder.equal(root.get("driver").get("email"), email);
    }

    public static Specification<Delivery> hasScheduledDate(LocalDate scheduledDate) {
        return scheduledDate == null ? null : (root, query, builder) -> builder.equal(root.get("scheduledDate"), scheduledDate);
    }
}
