package org.sopt.poti.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import org.sopt.poti.domain.user.entity.WithdrawalReason;

public record WithdrawRequest(
    @NotNull WithdrawalReason reason
) {
}
