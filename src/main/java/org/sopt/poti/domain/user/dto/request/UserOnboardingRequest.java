package org.sopt.poti.domain.user.dto.request;

public record UserOnboardingRequest(
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(min = 2 , max = 10)
        String nickname,
        Long favoriteArtistId
) {}
