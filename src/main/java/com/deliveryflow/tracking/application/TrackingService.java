package com.deliveryflow.tracking.application;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.tracking.api.TrackingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides the limited public tracking information available to customers.
 */
@Service
@Transactional(readOnly = true)
public class TrackingService {
    private final DeliveryRepository deliveryRepository;

    public TrackingService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Looks up a delivery when both the order number and recipient phone match.
     */
    public TrackingResponse trackByOrderNo(String orderNo, String recipientPhone) {
        return deliveryRepository.findByOrderOrderNoAndOrderRecipientPhone(orderNo, recipientPhone)
                .map(TrackingResponse::from)
                .orElseThrow(this::notFound);
    }

    /**
     * Looks up a delivery when both the tracking number and recipient phone match.
     */
    public TrackingResponse trackByTrackingNo(String trackingNo, String recipientPhone) {
        return deliveryRepository.findByTrackingNoAndOrderRecipientPhone(trackingNo, recipientPhone)
                .map(TrackingResponse::from)
                .orElseThrow(this::notFound);
    }

    // Use the same response for a missing number and a phone mismatch.
    private ApiException notFound() {
        return ApiException.notFound("error.delivery.notFound");
    }
}