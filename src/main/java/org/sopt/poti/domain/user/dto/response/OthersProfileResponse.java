package org.sopt.poti.domain.user.dto.response;

import java.time.LocalDate;

public record OthersProfileResponse(
        Long userId,
        String nickname,
        String profileImageUrl,
        Double ratingAvg,
        String activityMessage,
        LocalDate joinedAt,
        Summary participationSummary,
        Summary recruitSummary
) {
    public record Summary(
            Integer inProgress,
            Integer completed
    ) {}
}
