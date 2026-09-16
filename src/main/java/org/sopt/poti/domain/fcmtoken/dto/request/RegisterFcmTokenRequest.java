package org.sopt.poti.domain.fcmtoken.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.poti.domain.fcmtoken.entity.DeviceType;

public record RegisterFcmTokenRequest(
    @NotBlank String token,
    @NotNull DeviceType deviceType
) {

}
