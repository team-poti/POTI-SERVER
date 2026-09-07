package org.sopt.poti.domain.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageDirectory {
  POST("posts"),
  PROFILE("profiles"),
  REVIEW("reviews"),
  BANNER("banners"),
  ARTIST("artists");

  private final String prefix;
}
