package org.sopt.poti.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;

@Getter
@Entity
@Table(name = "payments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_payments_order_id",
        columnNames = "order_id"
    )
)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private Long version;

  @Column(name = "depositor", length = 50, nullable = false)
  private String depositor;

  @Column(name = "deposited_at", length = 30, nullable = false)
  private String depositedAt;

  @Column(nullable = false)
  private int amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  public static Payment create(
      String depositor,
      String depositedAt,
      int amount,
      Order order
  ) {
    Payment p = new Payment();
    p.depositor = depositor;
    p.depositedAt = depositedAt;
    p.amount = amount;
    p.order = order;
    return p;
  }
}