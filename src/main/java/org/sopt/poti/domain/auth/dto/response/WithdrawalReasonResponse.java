package org.sopt.poti.domain.auth.dto.response;

import org.sopt.poti.domain.user.entity.WithdrawalReason;

public record WithdrawalReasonResponse(String code, String label) {
    public static WithdrawalReasonResponse from(WithdrawalReason reason) {
        return new WithdrawalReasonResponse(reason.name(), reason.getLabel());
    }
}
