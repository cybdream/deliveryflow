package com.deliveryflow.delivery.domain;

import com.deliveryflow.order.domain.Order;
import com.deliveryflow.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries", uniqueConstraints = @UniqueConstraint(name = "uk_delivery_order", columnNames = "order_id"))
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "driver_id", nullable = false) private User driver;
    @Column(name = "scheduled_date", nullable = false) private LocalDate scheduledDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private DeliveryStatus status;
    @Column(name = "assigned_at", nullable = false, updatable = false) private LocalDateTime assignedAt;
    @Column(name = "delivered_at") private LocalDateTime deliveredAt;
    @Column(name = "tracking_no", unique = true, length = 30) private String trackingNo;
    @Version private Long version;

    protected Delivery() { }

    public Delivery(Order order, User driver, LocalDate scheduledDate, LocalDateTime assignedAt) {
        this(order, driver, scheduledDate, null, assignedAt);
    }

    public Delivery(Order order, User driver, LocalDate scheduledDate, String trackingNo, LocalDateTime assignedAt) {
        this.order = order;
        this.driver = driver;
        this.scheduledDate = scheduledDate;
        this.trackingNo = trackingNo;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = assignedAt;
    }

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public User getDriver() { return driver; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public DeliveryStatus getStatus() { return status; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public String getTrackingNo() { return trackingNo; }

    public DeliveryStatus changeStatus(DeliveryStatus nextStatus, LocalDateTime changedAt) {
        DeliveryStatus previousStatus = status;
        if (!isAllowedTransition(previousStatus, nextStatus)) {
            throw new IllegalArgumentException(previousStatus + " 상태에서 " + nextStatus + " 상태로 변경할 수 없습니다.");
        }
        status = nextStatus;
        if (nextStatus == DeliveryStatus.DELIVERED) { deliveredAt = changedAt; }
        return previousStatus;
    }

    private boolean isAllowedTransition(DeliveryStatus from, DeliveryStatus to) {
        return switch (from) {
            case ASSIGNED -> to == DeliveryStatus.IN_DELIVERY || to == DeliveryStatus.ON_HOLD || to == DeliveryStatus.CANCELLED;
            case IN_DELIVERY -> to == DeliveryStatus.DELIVERED || to == DeliveryStatus.ON_HOLD;
            case ON_HOLD -> to == DeliveryStatus.ASSIGNED || to == DeliveryStatus.IN_DELIVERY || to == DeliveryStatus.CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}