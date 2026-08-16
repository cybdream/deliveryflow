package com.deliveryflow.tracking.application;

import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.tracking.api.TrackingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class TrackingService {
    private final DeliveryRepository deliveryRepository;

    public TrackingService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public TrackingResponse trackByOrderNo(String orderNo, String recipientPhone) {
        return deliveryRepository.findByOrderOrderNoAndOrderRecipientPhone(orderNo, recipientPhone)
                .map(TrackingResponse::from)
                .orElseThrow(this::notFound);
    }

    public TrackingResponse trackByTrackingNo(String trackingNo, String recipientPhone) {
        return deliveryRepository.findByTrackingNoAndOrderRecipientPhone(trackingNo, recipientPhone)
                .map(TrackingResponse::from)
                .orElseThrow(this::notFound);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "배송 정보를 찾을 수 없습니다.");
    }
}
