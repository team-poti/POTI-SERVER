package org.sopt.poti.domain.groupbuy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "item_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "image_url", length = 255, nullable = false) // 이미지 URL은 필수
  private String imageUrl;

  @Column(name = "sort_order")
  private Integer sortOrder;

  // 양방향 연관관계 편의 메서드
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_buy_post_id", nullable = false)
  private GroupBuyPost groupBuyPost;

  @Builder
  private ItemImage(String imageUrl, Integer sortOrder, GroupBuyPost groupBuyPost) {
    this.imageUrl = imageUrl;
    this.sortOrder = sortOrder;
    this.groupBuyPost = groupBuyPost;
  }

  public static ItemImage create(String imageUrl, Integer sortOrder) {
    return ItemImage.builder()
        .imageUrl(imageUrl)
        .sortOrder(sortOrder)
        .build();
  }

}