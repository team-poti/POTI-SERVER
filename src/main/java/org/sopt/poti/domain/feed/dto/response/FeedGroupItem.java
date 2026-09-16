package org.sopt.poti.domain.feed.dto.response;

import lombok.Builder;

@Builder
public record FeedGroupItem(
    String artist,
    Long artistId,
    String postImage,
    String postTitle,
    Long postCount,
    String tag
) {

}
