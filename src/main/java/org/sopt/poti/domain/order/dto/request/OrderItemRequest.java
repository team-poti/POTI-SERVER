package org.sopt.poti.domain.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest (
        Long groupBuyOptionId,
        @Positive(message = "수량은 1 이상이어야 합니다.")
        @Max(value = 99, message = "수량은 99 이하여야 합니다.")
        int count
){}