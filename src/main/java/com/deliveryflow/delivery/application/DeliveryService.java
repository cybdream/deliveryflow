package com.deliveryflow.delivery.application;

import com.deliveryflow.delivery.api.CreateDeliveryRequest;
import com.deliveryflow.delivery.api.DeliveryHistoryResponse;
import com.deliveryflow.delivery.api.DeliveryResponse;
import com.deliveryflow.delivery.api.UpdateDeliveryStatusRequest;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryHistory;
import com.deliveryflow.delivery.domain.DeliveryHistoryRepository;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    public DeliveryService(DeliveryRepository deliveryRepository, DeliveryHistoryRepository deliveryHistoryRepository, OrderRepository orderRepository, UserRepository userRepository) { this.deliveryRepository = deliveryRepository; this.deliveryHistoryRepository = deliveryHistoryRepository; this.orderRepository = orderRepository; this.userRepository = userRepository; }

    @Transactional
    public DeliveryResponse assign(CreateDeliveryRequest request) {
        if (deliveryRepository.existsByOrderId(request.orderId())) { throw new IllegalArgumentException("이미 배송이 배정된 주문입니다."); }
        Order order = orderRepository.findById(request.orderId()).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        User driver = userRepository.findById(request.driverId()).filter(user -> user.getRole() == UserRole.DRIVER && user.isActive()).orElseThrow(() -> new IllegalArgumentException("활성 상태의 배송 기사를 찾을 수 없습니다."));
        Delivery savedDelivery = deliveryRepository.save(new Delivery(order, driver, request.scheduledDate(), LocalDateTime.now()));
        deliveryHistoryRepository.save(new DeliveryHistory(savedDelivery, "ASSIGNED", null, DeliveryStatus.ASSIGNED, "SYSTEM", null, savedDelivery.getAssignedAt()));
        return DeliveryResponse.from(savedDelivery);
    }

    @Transactional
    public DeliveryResponse updateStatus(Long deliveryId, UpdateDeliveryStatusRequest request) {
        if (requiresReason(request.status()) && (request.reason() == null || request.reason().isBlank())) { throw new IllegalArgumentException(request.status() + " 상태 변경에는 사유가 필요합니다."); }
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
        LocalDateTime changedAt = LocalDateTime.now();
        DeliveryStatus previousStatus = delivery.changeStatus(request.status(), changedAt);
        deliveryHistoryRepository.save(new DeliveryHistory(delivery, "STATUS_CHANGED", previousStatus, request.status(), request.changedBy(), request.reason(), changedAt));
        return DeliveryResponse.from(delivery);
    }

    public List<DeliveryHistoryResponse> findHistories(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) { throw new IllegalArgumentException("배송 정보를 찾을 수 없습니다."); }
        return deliveryHistoryRepository.findByDeliveryIdOrderByChangedAtAsc(deliveryId).stream().map(DeliveryHistoryResponse::from).toList();
    }
    private boolean requiresReason(DeliveryStatus status) { return status == DeliveryStatus.ON_HOLD || status == DeliveryStatus.CANCELLED; }
}
