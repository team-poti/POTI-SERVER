package org.sopt.poti.domain.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageDirectory {
  POST("posts"),
  PROFILE("profiles"),
  REVIEW("reviews");  // 아직 사진 리뷰는 없지만, 미리 추가

  private final String prefix;
}
