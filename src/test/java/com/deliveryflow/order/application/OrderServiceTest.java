package com.deliveryflow.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deliveryflow.order.api.CreateOrderRequest;
import com.deliveryflow.order.api.OrderResponse;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final OrderService orderService = new OrderService(orderRepository);

    @Test
    void createsOrderWithServerGeneratedOrderNumber() {
        when(orderRepository.existsByOrderNo(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(new CreateOrderRequest(
                "김민지", "010-1234-5678", "서울특별시 강남구 테헤란로 101", LocalDate.now()));

        assertThat(response.orderNo()).matches("ORD-\\d{8}-\\d{4}");
        assertThat(response.recipientName()).isEqualTo("김민지");
    }
}
