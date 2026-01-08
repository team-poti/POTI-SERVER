package org.sopt.poti.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int count;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "item_amount", nullable = false)
    private int itemAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_option_id", nullable = false)
    private GroupBuyOption groupBuyOption;

    public static OrderItem create(int count, int unitPrice, Order order, GroupBuyOption option) {
        OrderItem oi = new OrderItem();
        oi.count = count;
        oi.unitPrice = unitPrice;
        oi.itemAmount = count * unitPrice;
        oi.groupBuyOption = option;
        return oi;
    }
}