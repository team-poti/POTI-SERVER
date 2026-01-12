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
import org.sopt.poti.domain.artist.entity.Member;

@Getter
@Entity
@Table(name = "group_buy_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int price;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_buy_post_id", nullable = false)
  private GroupBuyPost groupBuyPost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Builder
  private GroupBuyOption(int price, GroupBuyPost groupBuyPost, Member member) {
    this.price = price;
    this.groupBuyPost = groupBuyPost;
    this.member = member;
  }

  public static GroupBuyOption create(int price, Member member) {
    return GroupBuyOption.builder()
        .price(price)
        .member(member)
        .build();
  }

}