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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries", uniqueConstraints = @UniqueConstraint(name = "uk_delivery_order", columnNames = "order_id"))
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    protected Delivery() {
    }

    public Delivery(Order order, User driver, LocalDate scheduledDate, LocalDateTime assignedAt) {
        this.order = order;
        this.driver = driver;
        this.scheduledDate = scheduledDate;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = assignedAt;
    }

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public User getDriver() { return driver; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public DeliveryStatus getStatus() { return status; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
}
