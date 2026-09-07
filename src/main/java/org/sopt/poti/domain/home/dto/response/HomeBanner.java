package org.sopt.poti.domain.home.dto.response;

import lombok.Builder;
import org.sopt.poti.domain.banner.entity.Banner;

@Builder
public record HomeBanner(
    Long id,
    String imageUrl,
    String deeplink
) {

  public static HomeBanner from(Banner banner) {
    return HomeBanner.builder()
        .id(banner.getId())
        .imageUrl(banner.getImageUrl())
        .deeplink(banner.getDeeplink())
        .build();
  }
}
