package org.sopt.poti.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

@NotBlank
public record DepositFormRequest (
        Long orderId,
        String depositorName,
        String depositedAt
){}