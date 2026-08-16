package com.deliveryflow.delivery.application;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/**
 * Generates a unique internal tracking number when a delivery is assigned.
 */
@Component
public class TrackingNumberGenerator {
    private static final int MAX_ATTEMPTS = 10;

    private final DeliveryRepository deliveryRepository;

    public TrackingNumberGenerator(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Returns a tracking number in the TRK-yyyyMMdd-######## format.
     */
    public String generate() {
        String prefix = "TRK-" + LocalDate.now().toString().replace("-", "") + "-";

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String trackingNo = prefix + ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
            if (!deliveryRepository.existsByTrackingNo(trackingNo)) {
                return trackingNo;
            }
        }

        throw ApiException.internal("error.delivery.trackingNumberUnavailable");
    }
}