package com.deliveryflow.delivery.application;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.api.CreateDeliveryRequest;
import com.deliveryflow.delivery.api.DeliveryHistoryResponse;
import com.deliveryflow.delivery.api.DeliveryResponse;
import com.deliveryflow.delivery.api.UpdateDeliveryStatusRequest;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryHistory;
import com.deliveryflow.delivery.domain.DeliveryHistoryRepository;
import com.deliveryflow.delivery.domain.DeliveryRepository;
import com.deliveryflow.delivery.domain.DeliverySpecifications;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Coordinates delivery assignment, status changes, history, and list queries.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TrackingNumberGenerator trackingNumberGenerator;
    private final DeliveryPermissionVerifier permissionVerifier;

    public DeliveryService(DeliveryRepository deliveryRepository,
            DeliveryHistoryRepository deliveryHistoryRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            TrackingNumberGenerator trackingNumberGenerator,
            DeliveryPermissionVerifier permissionVerifier) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.trackingNumberGenerator = trackingNumberGenerator;
        this.permissionVerifier = permissionVerifier;
    }

    /**
     * Assigns an active driver to an order and records the initial delivery history.
     */
    @Transactional
    public DeliveryResponse assign(CreateDeliveryRequest request) {
        validateOrderIsNotAssigned(request.orderId());

        // Look up only the order and driver that can participate in a new assignment.
        Order order = findOrder(request.orderId());
        User driver = findActiveDriver(request.driverId());

        // A new assignment always receives its own internal tracking number.
        Delivery delivery = new Delivery(order, driver, request.scheduledDate(),
                trackingNumberGenerator.generate(), LocalDateTime.now());
        Delivery savedDelivery = deliveryRepository.save(delivery);

        saveAssignmentHistory(savedDelivery);
        return DeliveryResponse.from(savedDelivery);
    }

    /**
     * Applies a permitted status transition and stores the reason in delivery history.
     */
    @Transactional
    public DeliveryResponse updateStatus(Long deliveryId, UpdateDeliveryStatusRequest request,
            String actorEmail, boolean admin) {
        Delivery delivery = findDelivery(deliveryId);

        // Authorization is checked before a driver can change a delivery state.
        permissionVerifier.verifyDeliveryAccess(delivery, actorEmail, admin);
        permissionVerifier.verifyStatusChangePermission(request.status(), admin);
        validateRequiredReason(request);

        LocalDateTime changedAt = LocalDateTime.now();
        DeliveryStatus previousStatus = delivery.changeStatus(request.status(), changedAt);
        saveStatusChangeHistory(delivery, previousStatus, request, actorEmail, changedAt);

        return DeliveryResponse.from(delivery);
    }

    /**
     * Returns the history of a delivery after verifying the caller can access it.
     */
    public List<DeliveryHistoryResponse> findHistories(Long deliveryId, String actorEmail, boolean admin) {
        Delivery delivery = findDelivery(deliveryId);
        permissionVerifier.verifyDeliveryAccess(delivery, actorEmail, admin);

        return deliveryHistoryRepository.findByDeliveryIdOrderByChangedAtAsc(deliveryId).stream()
                .map(DeliveryHistoryResponse::from)
                .toList();
    }

    /**
     * Returns deliveries for administrators with optional search conditions.
     */
    public Page<DeliveryResponse> findAll(DeliveryStatus status, Long driverId,
            LocalDate scheduledDate, Pageable pageable) {
        Specification<Delivery> specification = Specification.allOf(
                DeliverySpecifications.hasStatus(status),
                DeliverySpecifications.hasDriverId(driverId),
                DeliverySpecifications.hasScheduledDate(scheduledDate));

        return deliveryRepository.findAll(specification, pageable).map(DeliveryResponse::from);
    }

    /**
     * Returns only the deliveries assigned to the logged-in driver.
     */
    public Page<DeliveryResponse> findMine(String email, DeliveryStatus status,
            LocalDate scheduledDate, Pageable pageable) {
        Specification<Delivery> specification = Specification.allOf(
                DeliverySpecifications.hasDriverEmail(email),
                DeliverySpecifications.hasStatus(status),
                DeliverySpecifications.hasScheduledDate(scheduledDate));

        return deliveryRepository.findAll(specification, pageable).map(DeliveryResponse::from);
    }

    private void validateOrderIsNotAssigned(Long orderId) {
        if (deliveryRepository.existsByOrderId(orderId)) {
            throw ApiException.businessRule("error.delivery.alreadyAssigned");
        }
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.businessRule("error.order.notFound"));
    }

    private User findActiveDriver(Long driverId) {
        return userRepository.findById(driverId)
                .filter(user -> user.getRole() == UserRole.DRIVER && user.isActive())
                .orElseThrow(() -> ApiException.businessRule("error.driver.notFound"));
    }

    private Delivery findDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> ApiException.businessRule("error.delivery.notFound"));
    }

    private void validateRequiredReason(UpdateDeliveryStatusRequest request) {
        if (requiresReason(request.status()) && (request.reason() == null || request.reason().isBlank())) {
            throw ApiException.businessRule("error.delivery.reasonRequired", request.status());
        }
    }

    private void saveAssignmentHistory(Delivery delivery) {
        deliveryHistoryRepository.save(new DeliveryHistory(delivery, "ASSIGNED", null,
                DeliveryStatus.ASSIGNED, "SYSTEM", null, delivery.getAssignedAt()));
    }

    private void saveStatusChangeHistory(Delivery delivery, DeliveryStatus previousStatus,
            UpdateDeliveryStatusRequest request, String actorEmail, LocalDateTime changedAt) {
        deliveryHistoryRepository.save(new DeliveryHistory(delivery, "STATUS_CHANGED", previousStatus,
                request.status(), actorEmail, request.reason(), changedAt));
    }

    private boolean requiresReason(DeliveryStatus status) {
        return status == DeliveryStatus.ON_HOLD || status == DeliveryStatus.CANCELLED;
    }
}