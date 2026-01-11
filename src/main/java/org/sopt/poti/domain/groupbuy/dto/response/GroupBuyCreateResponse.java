package org.sopt.poti.domain.groupbuy.dto.response;

import lombok.Builder;

@Builder
public record GroupBuyCreateResponse(
    Long postId // 생성된 게시글 ID
) {
    public static GroupBuyCreateResponse of(Long postId) {
        return GroupBuyCreateResponse.builder()
                .postId(postId)
                .build();
    }
}
