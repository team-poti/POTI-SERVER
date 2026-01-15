package org.sopt.poti.domain.order.dto.request;

public record OrderItemRequest (
        Long groupBuyOptionId,
        int count
){}