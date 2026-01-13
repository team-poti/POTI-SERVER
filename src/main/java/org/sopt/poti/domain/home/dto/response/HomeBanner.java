package org.sopt.poti.domain.home.dto.response;

import lombok.Builder;

@Builder
public record HomeBanner(
    Long postId,
    String imageUrl
) {
}
