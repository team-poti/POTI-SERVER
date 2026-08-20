package org.sopt.poti.domain.user.dto.response;

import java.time.LocalDate;

public record MyPageResponse(
    Long userId,
    String nickname,
    String email,
    String profileImageUrl,
    Double ratingAvg,
    String activityMessage,
    LocalDate joinedAt,
    Boolean hasFavoriteArtist,
    Long favoriteArtistId,
    String favoriteArtistName,
    Summary participationSummary,
    Summary recruitSummary
) {

  public record Summary(
      Integer inProgress,
      Integer completed
  ) {

  }
}
