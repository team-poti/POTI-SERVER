package org.sopt.poti.domain.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "depositor", length = 50)
    private String depositor;

    @Column(name = "deposited_at")
    private LocalDateTime depositedAt;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public static Payment create(String depositor, int amount, Order order, LocalDateTime depositedAt) {
        Payment p = new Payment();
        p.depositor = depositor;
        p.amount = amount;
        p.order = order;
        p.depositedAt = depositedAt;
        p.status = PaymentStatus.PENDING;
        return p;
    }
}