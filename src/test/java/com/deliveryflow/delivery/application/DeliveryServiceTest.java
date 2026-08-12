package com.deliveryflow.delivery.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deliveryflow.delivery.api.CreateDeliveryRequest;
import com.deliveryflow.delivery.api.DeliveryResponse;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryRepository;
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
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final DeliveryService deliveryService = new DeliveryService(deliveryRepository, orderRepository, userRepository);

    @Test
    void assignsAnActiveDriverToAnOrder() {
        Order order = new Order("ORD-20260813-1001", "김민지", "010-1234-5678", "서울", LocalDate.now(), LocalDateTime.now());
        User driver = new User("driver1@deliveryflow.com", "홍길동", UserRole.DRIVER, true, LocalDateTime.now());
        when(deliveryRepository.existsByOrderId(1L)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(2L)).thenReturn(Optional.of(driver));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryResponse response = deliveryService.assign(new CreateDeliveryRequest(1L, 2L, LocalDate.now()));

        assertThat(response.status()).isEqualTo("ASSIGNED");
        assertThat(response.orderNo()).isEqualTo("ORD-20260813-1001");
        assertThat(response.driverName()).isEqualTo("홍길동");
    }

    @Test
    void rejectsAnOrderThatAlreadyHasDelivery() {
        when(deliveryRepository.existsByOrderId(1L)).thenReturn(true);

        assertThatThrownBy(() -> deliveryService.assign(new CreateDeliveryRequest(1L, 2L, LocalDate.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 배송이 배정된 주문입니다.");
    }
}
