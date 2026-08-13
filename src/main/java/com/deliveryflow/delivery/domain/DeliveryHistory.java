package com.deliveryflow.delivery.domain;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_histories")
public class DeliveryHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "delivery_id", nullable = false) private Delivery delivery;
    @Column(name = "action_type", nullable = false, length = 30) private String actionType;
    @Enumerated(EnumType.STRING) @Column(name = "previous_status", length = 20) private DeliveryStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(name = "current_status", nullable = false, length = 20) private DeliveryStatus currentStatus;
    @Column(name = "changed_by", nullable = false, length = 50) private String changedBy;
    @Column(length = 300) private String reason;
    @Column(name = "changed_at", nullable = false, updatable = false) private LocalDateTime changedAt;

    protected DeliveryHistory() { }
    public DeliveryHistory(Delivery delivery, String actionType, DeliveryStatus previousStatus, DeliveryStatus currentStatus, String changedBy, String reason, LocalDateTime changedAt) {
        this.delivery = delivery; this.actionType = actionType; this.previousStatus = previousStatus; this.currentStatus = currentStatus; this.changedBy = changedBy; this.reason = reason; this.changedAt = changedAt;
    }
    public Long getId() { return id; }
    public String getActionType() { return actionType; }
    public DeliveryStatus getPreviousStatus() { return previousStatus; }
    public DeliveryStatus getCurrentStatus() { return currentStatus; }
    public String getChangedBy() { return changedBy; }
    public String getReason() { return reason; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
