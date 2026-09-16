package org.sopt.poti.domain.payment.dto.response;

import java.time.LocalDateTime;
import org.sopt.poti.domain.order.entity.OrderStatus;

public record OrderConfirmResponse(
    Long orderId,
    OrderStatus status,
    LocalDateTime confirmedAt
) {

}
