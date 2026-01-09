package org.sopt.poti.domain.delivery.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public static Delivery create(Order order) {
        Delivery d = new Delivery();
        d.order = order;
        d.status = OrderStatus.READY;
        return d;
    }
}