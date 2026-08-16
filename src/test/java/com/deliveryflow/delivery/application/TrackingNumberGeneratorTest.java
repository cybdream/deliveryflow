package com.deliveryflow.delivery.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TrackingNumberGeneratorTest {
    private final DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
    private final TrackingNumberGenerator generator = new TrackingNumberGenerator(deliveryRepository);

    @Test
    void generatesAnUnusedTrackingNumber() {
        when(deliveryRepository.existsByTrackingNo(anyString())).thenReturn(false);

        String trackingNo = generator.generate();

        assertThat(trackingNo).matches("TRK-" + LocalDate.now().toString().replace("-", "") + "-[0-9]{8}");
        verify(deliveryRepository).existsByTrackingNo(trackingNo);
    }

    @Test
    void failsAfterRepeatedTrackingNumberCollisions() {
        when(deliveryRepository.existsByTrackingNo(anyString())).thenReturn(true);

        assertThatThrownBy(generator::generate)
                .isInstanceOf(ApiException.class)
                .hasMessage("error.delivery.trackingNumberUnavailable");
    }
}