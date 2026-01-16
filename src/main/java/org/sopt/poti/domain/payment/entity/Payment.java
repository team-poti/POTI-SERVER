package org.sopt.poti.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "payments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_payments_order_id",
        columnNames = "order_id"
    )
)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

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

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  public static Payment create(String depositor, int amount, Order order, String depositedAt) {
    Payment p = new Payment();
    p.depositor = depositor;
    p.amount = amount;
    p.order = order;
    p.depositedAt = depositedAt;
    return p;
  }

  public void submitDepositForm(String depositorName, String depositedAt) {
    this.depositor = depositorName;
    this.depositedAt = depositedAt;
  }

  public void confirm(LocalDateTime confirmedAt) {
    this.confirmedAt = confirmedAt;
  }
}