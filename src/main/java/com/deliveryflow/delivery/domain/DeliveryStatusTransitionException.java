package com.deliveryflow.delivery.domain;

/**
 * Signals an attempted delivery status transition that is not allowed.
 */
public class DeliveryStatusTransitionException extends RuntimeException {
    private final DeliveryStatus previousStatus;
    private final DeliveryStatus nextStatus;

    public DeliveryStatusTransitionException(DeliveryStatus previousStatus, DeliveryStatus nextStatus) {
        super(previousStatus + " -> " + nextStatus);
        this.previousStatus = previousStatus;
        this.nextStatus = nextStatus;
    }

    public DeliveryStatus getPreviousStatus() { return previousStatus; }
    public DeliveryStatus getNextStatus() { return nextStatus; }
}