package org.sopt.poti.domain.home.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record HomeResponse(
    String nickname,
    String mainArtist, // 최애 아티스트명
    Long mainArtistId,
    List<HomeGroupBuyItem> myGroupItems,
    List<HomeGroupBuyItem> otherGroupItems,
    List<HomeBanner> banners
) {

}
