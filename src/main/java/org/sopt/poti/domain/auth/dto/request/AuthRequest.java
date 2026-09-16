package org.sopt.poti.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.poti.domain.user.entity.SocialType;

public record AuthRequest(
    @NotNull(message = "소셜 타입은 필수입니다.")
    SocialType socialType,

    @NotBlank(message = "토큰은 필수입니다.")
    String token,

    String name
) {
}
