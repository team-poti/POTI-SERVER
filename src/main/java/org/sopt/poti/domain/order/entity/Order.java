package org.sopt.poti.domain.order.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.payment.entity.Payment;
import org.sopt.poti.domain.review.entity.Review;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.entity.BaseTimeEntity;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalAmount;

    @Embedded
    private DeliveryInfo deliveryInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_post_id", nullable = false)
    private GroupBuyPost groupBuyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_method_id", nullable = false)
    private DeliveryMethod deliveryMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Review review;

    @Builder
    private Order(
            GroupBuyPost groupBuyPost,
            User user,
            DeliveryMethod deliveryMethod,
            int totalAmount,
            DeliveryInfo deliveryInfo
    ) {
        this.groupBuyPost = groupBuyPost;
        this.user = user;
        this.deliveryMethod = deliveryMethod;
        this.totalAmount = totalAmount;
        this.deliveryInfo = deliveryInfo;
        this.status = OrderStatus.WAIT_PAY;
    }

    public static Order create(
            GroupBuyPost post,
            User user,
            DeliveryMethod method,
            int totalAmount,
            DeliveryInfo deliveryInfo
    ) {
        return Order.builder()
                .groupBuyPost(post)
                .user(user)
                .deliveryMethod(method)
                .totalAmount(totalAmount)
                .deliveryInfo(deliveryInfo)
                .build();
    }
    public void requestPayCheck() {
        if (this.status != OrderStatus.WAIT_PAY) {
            throw new BusinessException(ErrorStatus.ORDER_NOT_WAIT_PAY);
        }
        this.status = OrderStatus.WAIT_PAY_CHECK;
    }
}