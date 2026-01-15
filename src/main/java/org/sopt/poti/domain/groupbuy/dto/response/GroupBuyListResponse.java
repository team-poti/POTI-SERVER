package org.sopt.poti.domain.groupbuy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record GroupBuyListResponse(
    @Schema(description = "상품명", example = "NewJeans How Sweet")
    String postTitle,
    @Schema(description = "아티스트 ID", example = "1")
    Long artistId,
    @Schema(description = "아티스트명", example = "NewJeans")
    String artist,
    @Schema(description = "현재 페이지 번호", example = "0")
    int currentPage,
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,
    List<GroupBuyPotItem> pots
) {
    public static GroupBuyListResponse of(String postTitle, Long artistId, String artist, int currentPage, boolean hasNext, List<GroupBuyPotItem> pots) {
        return GroupBuyListResponse.builder()
                .postTitle(postTitle)
                .artistId(artistId)
                .artist(artist)
                .currentPage(currentPage)
                .hasNext(hasNext)
                .pots(pots)
                .build();
    }
}
