package org.sopt.poti.domain.feed.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record FeedResponse(
    String nickname,
    String mainArtist,
    Long mainArtistId,
    boolean hasNext,
    List<FeedGroupItem> groupItems
) {

  public static FeedResponse of(String nickname, String mainArtist, Long mainArtistId,
      boolean hasNext,
      List<FeedGroupItem> groupItems) {
    return FeedResponse.builder()
        .nickname(nickname)
        .mainArtist(mainArtist)
        .mainArtistId(mainArtistId)
        .hasNext(hasNext)
        .groupItems(groupItems)
        .build();
  }
}
