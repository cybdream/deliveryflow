package com.deliveryflow.tracking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class TrackingServiceTest {
    private final DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
    private final TrackingService trackingService = new TrackingService(deliveryRepository);

    @Test
    void returnsLimitedDeliveryInformationWhenOrderAndPhoneMatch() {
        Delivery delivery = delivery();
        when(deliveryRepository.findByOrderOrderNoAndOrderRecipientPhone("ORD-20260816-8503", "010-1234-5678"))
                .thenReturn(Optional.of(delivery));

        var response = trackingService.trackByOrderNo("ORD-20260816-8503", "010-1234-5678");

        assertThat(response.orderNo()).isEqualTo("ORD-20260816-8503");
        assertThat(response.trackingNo()).isEqualTo("TRK-20260816-12345678");
        assertThat(response.status()).isEqualTo("IN_DELIVERY");
        assertThat(response.scheduledDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    void returnsLimitedDeliveryInformationWhenTrackingNumberAndPhoneMatch() {
        Delivery delivery = delivery();
        when(deliveryRepository.findByTrackingNoAndOrderRecipientPhone("TRK-20260816-12345678", "010-1234-5678"))
                .thenReturn(Optional.of(delivery));

        var response = trackingService.trackByTrackingNo("TRK-20260816-12345678", "010-1234-5678");

        assertThat(response.orderNo()).isEqualTo("ORD-20260816-8503");
        assertThat(response.trackingNo()).isEqualTo("TRK-20260816-12345678");
    }

    @Test
    void hidesWhetherAnOrderExistsWhenPhoneDoesNotMatch() {
        when(deliveryRepository.findByOrderOrderNoAndOrderRecipientPhone("ORD-20260816-8503", "010-9999-9999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackingService.trackByOrderNo("ORD-20260816-8503", "010-9999-9999"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("배송 정보를 찾을 수 없습니다.");
    }

    private Delivery delivery() {
        Order order = new Order("ORD-20260816-8503", "김민지", "010-1234-5678", "서울시 중구 1",
                LocalDate.now().plusDays(1), LocalDateTime.now());
        Delivery delivery = new Delivery(order,
                new User("driver@test.local", "Test Driver", UserRole.DRIVER, true, LocalDateTime.now()),
                LocalDate.now().plusDays(1), "TRK-20260816-12345678", LocalDateTime.now());
        delivery.changeStatus(DeliveryStatus.IN_DELIVERY, LocalDateTime.now());
        return delivery;
    }
}