package org.sopt.poti.domain.order.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.payment.entity.Payment;
import org.sopt.poti.domain.review.entity.Review;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalAmount;

    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    @Column(length = 10)
    private String zipcode;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(length = 20)
    private String phone;

    @Column(name = "request_info", columnDefinition = "TEXT")
    private String requestInfo;

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

    public static Order create(GroupBuyPost post, User user, DeliveryMethod method, int totalAmount) {
        Order order = new Order();
        order.groupBuyPost = post;
        order.user = user;
        order.deliveryMethod = method;
        order.totalAmount = totalAmount;
        order.status = OrderStatus.WAIT_PAY;
        return order;
    }
}