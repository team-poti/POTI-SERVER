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
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.order.entity.Order;

@Getter
@Entity
@Table(name = "group_buy_shippings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyShipping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int price; // 해당 공구에서의 배송비

  // 양방향 연관관계 편의 메서드
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_buy_post_id", nullable = false)
  private GroupBuyPost groupBuyPost;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_method_id", nullable = false)
  private DeliveryMethod deliveryMethod;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Builder
  private GroupBuyShipping(int price, GroupBuyPost groupBuyPost, DeliveryMethod deliveryMethod,
      Order order) {
    this.price = price;
    this.groupBuyPost = groupBuyPost;
    this.deliveryMethod = deliveryMethod;
    this.order = order;
  }

  public static GroupBuyShipping create(int price, DeliveryMethod deliveryMethod, Order order) {
    return GroupBuyShipping.builder()
        .price(price)
        .deliveryMethod(deliveryMethod)
        .order(order)
        .build();
  }

}
