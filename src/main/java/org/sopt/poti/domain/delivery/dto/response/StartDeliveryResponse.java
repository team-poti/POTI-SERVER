package org.sopt.poti.domain.delivery.dto.response;

import java.time.LocalDateTime;
import org.sopt.poti.domain.order.entity.OrderStatus;

public record StartDeliveryResponse(
    Long orderId,
    OrderStatus status,
    String trackingNumber,
    LocalDateTime shippedAt
) {

}
