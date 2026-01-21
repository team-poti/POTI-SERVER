package org.sopt.poti.domain.item.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Entity
@Getter
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String artist;   // 아티스트 (IVE, NewJeans...) -> 나중에 Artist 테이블로 뺄 수도 있음
  private String name;     // 상품명 (LOVE DIVE, I AM...)

  @Enumerated(EnumType.STRING) // 👈 핵심! 별도 테이블 없이 문자열로 저장
  private ItemCategory category;

  private String imageUrl; // 대표 이미지
}

