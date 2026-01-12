package org.sopt.poti.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserOnboardingRequest(
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 2 , max = 10)
        String nickname,
        Long favoriteArtistId
) {}
