package org.sopt.poti.domain.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;

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

    @Column(name = "deposited_at", length = 30)
    private String depositedAt;

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

    public static Payment create(String depositor, int amount, Order order, String depositedAt) {
        Payment p = new Payment();
        p.depositor = depositor;
        p.amount = amount;
        p.order = order;
        p.depositedAt = depositedAt;
        p.status = PaymentStatus.PENDING;
        return p;
    }

    public void submitDepositForm(String depositorName, String depositedAt) {
        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorStatus.PAYMENT_NOT_PENDING);
        }
        this.depositor = depositorName;
        this.depositedAt = depositedAt;
        this.status = PaymentStatus.REQUESTED;
    }

    public void confirm(LocalDateTime confirmedAt) {
        if (this.status != PaymentStatus.REQUESTED) {
            throw new BusinessException(ErrorStatus.PAYMENT_NOT_REQUESTED);
        }
        this.status = PaymentStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }
}