package org.sopt.poti.domain.auth.dto.request;

import jakarta.validation.constraints.Size;

public record WithdrawRequest(
    @Size(max = 500) String reason
) {

}
