package org.sopt.poti.domain.banner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "banners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  @Column(length = 500)
  private String deeplink;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(nullable = false)
  private boolean active = true;

  @Builder
  private Banner(String imageUrl, String deeplink, int sortOrder) {
    this.imageUrl = imageUrl;
    this.deeplink = deeplink;
    this.sortOrder = sortOrder;
    this.active = true;
  }

  public void update(String imageUrl, String deeplink, int sortOrder, boolean active) {
    this.imageUrl = imageUrl;
    this.deeplink = deeplink;
    this.sortOrder = sortOrder;
    this.active = active;
  }
}
