package org.sopt.poti.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenReissueResponse(
    String accessToken,
    String refreshToken
) {
}