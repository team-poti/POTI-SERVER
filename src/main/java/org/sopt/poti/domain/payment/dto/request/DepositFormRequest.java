package org.sopt.poti.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepositFormRequest (
        @NotNull Long orderId,
        @NotBlank String depositorName,
        @NotBlank String depositedAt
){}