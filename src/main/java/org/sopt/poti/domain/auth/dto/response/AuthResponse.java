package org.sopt.poti.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken,
    boolean isNewUser,
    Long userId
) {
}
