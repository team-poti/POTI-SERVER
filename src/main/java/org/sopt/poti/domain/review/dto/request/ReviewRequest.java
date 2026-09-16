package org.sopt.poti.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull(message = "거래 ID는 필수 입력 값입니다.")
        Long transactionId,

        @NotNull(message = "별점은 필수 입력 값입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
        Integer star

) {}