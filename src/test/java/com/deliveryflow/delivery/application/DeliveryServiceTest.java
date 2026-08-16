package com.deliveryflow.delivery.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.api.CreateDeliveryRequest;
import com.deliveryflow.delivery.api.DeliveryResponse;
import com.deliveryflow.delivery.api.UpdateDeliveryStatusRequest;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryHistoryRepository;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import com.deliveryflow.delivery.domain.DeliveryStatusTransitionException;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeliveryServiceTest {
    private final DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
    private final DeliveryHistoryRepository deliveryHistoryRepository = Mockito.mock(DeliveryHistoryRepository.class);
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final TrackingNumberGenerator trackingNumberGenerator = Mockito.mock(TrackingNumberGenerator.class);
    private final DeliveryPermissionVerifier permissionVerifier = new DeliveryPermissionVerifier();
    private final DeliveryService deliveryService = new DeliveryService(deliveryRepository, deliveryHistoryRepository,
            orderRepository, userRepository, trackingNumberGenerator, permissionVerifier);

    @Test
    void assignsAnActiveDriverToAnOrder() {
        Order order = order();
        User driver = activeDriver();
        when(deliveryRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(2L)).thenReturn(Optional.of(driver));
        when(trackingNumberGenerator.generate()).thenReturn("TRK-20260816-12345678");
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryResponse response = deliveryService.assign(new CreateDeliveryRequest(1L, 2L, LocalDate.now()));

        assertThat(response.status()).isEqualTo("ASSIGNED");
        assertThat(response.orderNo()).isEqualTo("ORD-20260813-1001");
        assertThat(response.trackingNo()).isEqualTo("TRK-20260816-12345678");
        assertThat(response.driverName()).isEqualTo("홍길동");
    }

    @Test
    void rejectsAnOrderThatAlreadyHasDelivery() {
        when(deliveryRepository.existsByOrderId(1L)).thenReturn(true);

        assertThatThrownBy(() -> deliveryService.assign(new CreateDeliveryRequest(1L, 2L, LocalDate.now())))
                .isInstanceOf(ApiException.class)
                .hasMessage("error.delivery.alreadyAssigned");
    }

    @Test
    void changesAssignedDeliveryToInDelivery() {
        Delivery delivery = delivery();
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        DeliveryResponse response = deliveryService.updateStatus(1L,
                new UpdateDeliveryStatusRequest(DeliveryStatus.IN_DELIVERY, null),
                "driver1@deliveryflow.com", false);

        assertThat(response.status()).isEqualTo("IN_DELIVERY");
    }

    @Test
    void requiresReasonWhenDeliveryIsPutOnHold() {
        Delivery delivery = delivery();
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.updateStatus(1L,
                new UpdateDeliveryStatusRequest(DeliveryStatus.ON_HOLD, null),
                "driver1@deliveryflow.com", false))
                .isInstanceOf(ApiException.class)
                .hasMessage("error.delivery.reasonRequired");
    }

    @Test
    void rejectsStatusChangeAfterDeliveryIsCompleted() {
        Delivery delivery = delivery();
        delivery.changeStatus(DeliveryStatus.IN_DELIVERY, LocalDateTime.now());
        delivery.changeStatus(DeliveryStatus.DELIVERED, LocalDateTime.now());
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.updateStatus(1L,
                new UpdateDeliveryStatusRequest(DeliveryStatus.ON_HOLD, "재배송 요청"),
                "driver1@deliveryflow.com", false))
                .isInstanceOf(DeliveryStatusTransitionException.class)
                .hasMessage("DELIVERED -> ON_HOLD");
    }

    @Test
    void preventsDriverFromReturningHeldDeliveryToAssigned() {
        Delivery delivery = delivery();
        delivery.changeStatus(DeliveryStatus.ON_HOLD, LocalDateTime.now());
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.updateStatus(1L,
                new UpdateDeliveryStatusRequest(DeliveryStatus.ASSIGNED, null),
                "driver1@deliveryflow.com", false))
                .isInstanceOf(ApiException.class)
                .hasMessage("error.delivery.driverStatusForbidden");
    }

    @Test
    void preventsDriverFromViewingAnotherDriversHistory() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery()));

        assertThatThrownBy(() -> deliveryService.findHistories(1L, "another-driver@deliveryflow.com", false))
                .isInstanceOf(ApiException.class)
                .hasMessage("error.delivery.accessDenied");
    }

    private Order order() {
        return new Order("ORD-20260813-1001", "김민지", "010-1234-5678", "서울",
                LocalDate.now(), LocalDateTime.now());
    }

    private User activeDriver() {
        return new User("driver1@deliveryflow.com", "홍길동", UserRole.DRIVER, true, LocalDateTime.now());
    }

    private Delivery delivery() {
        return new Delivery(order(), activeDriver(), LocalDate.now(), LocalDateTime.now());
    }
}