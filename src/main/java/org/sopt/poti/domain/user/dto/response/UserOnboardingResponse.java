package org.sopt.poti.domain.user.dto.response;

public record UserOnboardingResponse(
        String nickname,
        Long favoriteArtistId
) {}