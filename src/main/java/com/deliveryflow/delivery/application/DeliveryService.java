package com.deliveryflow.delivery.application;

import com.deliveryflow.delivery.api.CreateDeliveryRequest;
import com.deliveryflow.delivery.api.DeliveryResponse;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DeliveryResponse assign(CreateDeliveryRequest request) {
        if (deliveryRepository.existsByOrderId(request.orderId())) {
            throw new IllegalArgumentException("이미 배송이 배정된 주문입니다.");
        }

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        User driver = userRepository.findById(request.driverId())
                .filter(user -> user.getRole() == UserRole.DRIVER && user.isActive())
                .orElseThrow(() -> new IllegalArgumentException("활성 상태의 배송 기사를 찾을 수 없습니다."));

        Delivery delivery = new Delivery(order, driver, request.scheduledDate(), LocalDateTime.now());
        return DeliveryResponse.from(deliveryRepository.save(delivery));
    }
}
