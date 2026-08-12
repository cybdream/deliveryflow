package com.deliveryflow.order.application;

import com.deliveryflow.order.api.CreateOrderRequest;
import com.deliveryflow.order.api.OrderResponse;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        String orderNo = generateOrderNo();
        Order order = new Order(orderNo, request.recipientName(), request.recipientPhone(),
                request.address(), request.requestedDate(), LocalDateTime.now());
        return OrderResponse.from(orderRepository.save(order));
    }

    public Page<OrderResponse> findAll(String keyword, Pageable pageable) {
        Page<Order> orders = (keyword == null || keyword.isBlank())
                ? orderRepository.findAll(pageable)
                : orderRepository.findByRecipientNameContainingIgnoreCaseOrOrderNoContainingIgnoreCase(
                        keyword, keyword, pageable);
        return orders.map(OrderResponse::from);
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(ORDER_DATE_FORMAT);
        for (int attempt = 0; attempt < 10; attempt++) {
            String orderNo = "ORD-" + date + "-" + ThreadLocalRandom.current().nextInt(1000, 10000);
            if (!orderRepository.existsByOrderNo(orderNo)) {
                return orderNo;
            }
        }
        throw new IllegalStateException("주문번호를 생성하지 못했습니다. 다시 시도해 주세요.");
    }
}
