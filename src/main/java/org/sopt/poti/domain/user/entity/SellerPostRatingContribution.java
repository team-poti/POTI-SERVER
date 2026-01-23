package org.sopt.poti.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "seller_post_rating_contributions",
    uniqueConstraints = @UniqueConstraint(name = "uk_seller_post", columnNames = {"seller_id",
        "post_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerPostRatingContribution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "seller_id", nullable = false)
  private Long sellerId;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "applied_avg", nullable = false)
  private double appliedAvg;      // 해당 post가 seller에 반영된 평균값

  @Column(name = "applied_weight", nullable = false)
  private double appliedWeight;   // 해당 post가 seller에 반영된 가중치

  @Builder
  private SellerPostRatingContribution(Long sellerId, Long postId, double appliedAvg,
      double appliedWeight) {
    this.sellerId = sellerId;
    this.postId = postId;
    this.appliedAvg = appliedAvg;
    this.appliedWeight = appliedWeight;
  }

  public static SellerPostRatingContribution create(Long sellerId, Long postId) {
    return SellerPostRatingContribution.builder()
        .sellerId(sellerId)
        .postId(postId)
        .appliedAvg(0.0)
        .appliedWeight(0.0)
        .build();
  }

  public void update(double avg, double weight) {
    this.appliedAvg = avg;
    this.appliedWeight = weight;
  }
}