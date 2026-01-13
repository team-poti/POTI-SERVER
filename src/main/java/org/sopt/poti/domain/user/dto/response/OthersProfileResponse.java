package org.sopt.poti.domain.user.dto.response;

import java.time.LocalDate;

public record OthersProfileResponse(
        Long userId,
        String email,
        String nickname,
        String profileImageUrl,
        Double ratingAvg,
        String activityMessage,
        LocalDate joinedAt,
        Boolean hasFavoriteArtist,
        Summary recruitSummary
) {
    public record Summary(
            Integer total,
            Integer inProgress,
            Integer completed
    ) {}
}

