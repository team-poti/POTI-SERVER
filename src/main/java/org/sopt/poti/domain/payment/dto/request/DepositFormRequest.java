package org.sopt.poti.domain.payment.dto.request;

public record DepositFormRequest (
        Long orderId,
        String depositorName,
        String depositedAt
){}