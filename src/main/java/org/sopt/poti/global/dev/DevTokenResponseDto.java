package org.sopt.poti.global.dev;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DevTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
