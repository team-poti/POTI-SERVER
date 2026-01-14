package org.sopt.poti.domain.feed.dto.response;

import lombok.Builder;

@Builder
public record FeedGroupItem(
    String artist,
    String postImage,
    String postTitle,
    Long postCount,
    String tag
) {
}
