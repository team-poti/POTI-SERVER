package org.sopt.poti.domain.delivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;

@Getter
@Entity
@Table(name = "deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tracking_number", length = 50)
  private String trackingNumber;

  @Column(nullable = false)
  private String carrier; // 배송사

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, unique = true)
  private Order order;

  @Builder
  public Delivery(String trackingNumber, String carrier,
      LocalDateTime shippedAt,
      Order order) {
    this.trackingNumber = trackingNumber;
    this.carrier = carrier;
    this.shippedAt = shippedAt;
    this.order = order;
  }
  
}