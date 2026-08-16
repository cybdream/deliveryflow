package com.deliveryflow.delivery.application;

import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.delivery.domain.Delivery;
import com.deliveryflow.delivery.domain.DeliveryStatus;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Centralizes delivery access rules for administrators and drivers.
 */
@Component
public class DeliveryPermissionVerifier {
    private static final Set<DeliveryStatus> DRIVER_CHANGEABLE_STATUSES =
            EnumSet.of(DeliveryStatus.IN_DELIVERY, DeliveryStatus.DELIVERED, DeliveryStatus.ON_HOLD);

    /**
     * Confirms that the current user can view or change the target delivery.
     */
    public void verifyDeliveryAccess(Delivery delivery, String actorEmail, boolean admin) {
        if (!admin && !delivery.getDriver().getEmail().equals(actorEmail)) {
            throw ApiException.forbidden("error.delivery.accessDenied");
        }
    }

    /**
     * Confirms that a driver is only requesting a status they are allowed to set.
     */
    public void verifyStatusChangePermission(DeliveryStatus nextStatus, boolean admin) {
        if (!admin && !DRIVER_CHANGEABLE_STATUSES.contains(nextStatus)) {
            throw ApiException.forbidden("error.delivery.driverStatusForbidden");
        }
    }
}