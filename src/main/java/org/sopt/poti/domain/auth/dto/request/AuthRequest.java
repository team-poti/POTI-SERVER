package org.sopt.poti.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
    @NotBlank(message = "소셜 타입은 필수입니다.")
    String socialType, // Enum 매핑 전 String으로 받아서 검증

    @NotBlank(message = "토큰은 필수입니다.")
    String token
) {

}
