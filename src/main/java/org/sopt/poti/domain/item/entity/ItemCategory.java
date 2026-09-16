package org.sopt.poti.domain.item.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemCategory {
  PHOTO_CARD("포토카드"),
  ALBUM("앨범"),
  DOLL("인형"),
  SEASONS_GREETING("시즌 그리팅"),
  ETC("기타");

  private final String description;
}

