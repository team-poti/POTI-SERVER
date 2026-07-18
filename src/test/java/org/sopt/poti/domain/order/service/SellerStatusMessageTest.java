package org.sopt.poti.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;

/**
 * 판매자 상세 화면 상태 메시지 분기 테스트
 *
 * CLOSED 상태에서 주문 조합에 따른 메시지 검증:
 * - WAIT_PAY 존재 시 → "입금을 기다리는 중이에요"
 * - WAIT_PAY 없고 WAIT_PAY_CHECK 존재 시 → "입금 확인을 기다리는 참여자가 있어요"
 */
@DisplayName("판매자 상세 - CLOSED 상태 메시지 분기")
class SellerStatusMessageTest {

    // hasWaitPayCheckWithoutWaitPay 로직을 직접 추출하여 테스트
    private boolean hasWaitPayCheckWithoutWaitPay(List<Order> orders) {
        boolean hasWaitPay = orders.stream()
            .anyMatch(o -> o.getStatus() == OrderStatus.WAIT_PAY);
        boolean hasWaitPayCheck = orders.stream()
            .anyMatch(o -> o.getStatus() == OrderStatus.WAIT_PAY_CHECK);
        return !hasWaitPay && hasWaitPayCheck;
    }

    private String resolveMessage(GroupBuyPost post, List<Order> orders) {
        String message = post.getStatus().getSellerMessage();
        if (post.getStatus() == GroupBuyPostStatus.CLOSED
            && hasWaitPayCheckWithoutWaitPay(orders)) {
            message = "입금 확인을 기다리는 참여자가 있어요";
        }
        return message;
    }

    private Order orderWithStatus(OrderStatus status) {
        Order order = mock(Order.class);
        when(order.getStatus()).thenReturn(status);
        return order;
    }

    private GroupBuyPost closedPost() {
        GroupBuyPost post = mock(GroupBuyPost.class);
        when(post.getStatus()).thenReturn(GroupBuyPostStatus.CLOSED);
        return post;
    }

    @Test
    @DisplayName("모두 WAIT_PAY → 입금 대기 메시지")
    void allWaitPay() {
        List<Order> orders = List.of(
            orderWithStatus(OrderStatus.WAIT_PAY),
            orderWithStatus(OrderStatus.WAIT_PAY)
        );

        String message = resolveMessage(closedPost(), orders);

        assertThat(message).isEqualTo("입금을 기다리는 중이에요");
    }

    @Test
    @DisplayName("WAIT_PAY + WAIT_PAY_CHECK 혼재 → 아직 미입금자 있으므로 입금 대기 메시지")
    void mixWaitPayAndWaitPayCheck() {
        List<Order> orders = List.of(
            orderWithStatus(OrderStatus.WAIT_PAY),
            orderWithStatus(OrderStatus.WAIT_PAY_CHECK)
        );

        String message = resolveMessage(closedPost(), orders);

        assertThat(message).isEqualTo("입금을 기다리는 중이에요");
    }

    @Test
    @DisplayName("모두 WAIT_PAY_CHECK → 입금 확인 대기 메시지")
    void allWaitPayCheck() {
        List<Order> orders = List.of(
            orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
            orderWithStatus(OrderStatus.WAIT_PAY_CHECK)
        );

        String message = resolveMessage(closedPost(), orders);

        assertThat(message).isEqualTo("입금 확인을 기다리는 참여자가 있어요");
    }

    @Test
    @DisplayName("WAIT_PAY_CHECK + PAID 혼재 → 모두 입금했으므로 입금 확인 대기 메시지")
    void mixWaitPayCheckAndPaid() {
        List<Order> orders = List.of(
            orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
            orderWithStatus(OrderStatus.PAID)
        );

        String message = resolveMessage(closedPost(), orders);

        assertThat(message).isEqualTo("입금 확인을 기다리는 참여자가 있어요");
    }

    @Test
    @DisplayName("WAIT_PAY + WAIT_PAY_CHECK + PAID 혼재 → 미입금자 있으므로 입금 대기 메시지")
    void mixAllThree() {
        List<Order> orders = List.of(
            orderWithStatus(OrderStatus.WAIT_PAY),
            orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
            orderWithStatus(OrderStatus.PAID)
        );

        String message = resolveMessage(closedPost(), orders);

        assertThat(message).isEqualTo("입금을 기다리는 중이에요");
    }
}
