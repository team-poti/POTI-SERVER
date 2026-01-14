package org.sopt.poti.domain.home.dto.response;

import lombok.Builder;

@Builder
public record HomeGroupBuyItem(
    String title,           // 상품명 (분철글 제목)
    String artist,          // 아티스트 명
    String postImage,       // 대표 이미지 (최신글 썸네일)
    Long postCount,        // 해당 상품명의 총 게시글 수
    String tag              // 태그 (예: "인기")
) {
}
