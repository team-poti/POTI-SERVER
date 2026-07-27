package org.sopt.poti.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;

/**
 * 판매자 상세 화면 상태 메시지 분기 테스트
 *
 * CLOSED 상태에서 주문 조합에 따른 메시지 검증:
 * - WAIT_PAY 존재 → "입금을 기다리는 중이에요"
 * - WAIT_PAY 없고 WAIT_PAY_CHECK 존재 → "입금 확인을 기다리는 참여자가 있어요"
 *
 * CLOSED 외 상태는 GroupBuyPostStatus.sellerMessage 그대로 반환.
 */
@DisplayName("판매자 상세 - CLOSED 상태 메시지 분기")
class SellerStatusMessageTest {

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

    @Nested
    @DisplayName("CLOSED 상태 - 정상 케이스")
    class ClosedStatus {

        @Test
        @DisplayName("모두 WAIT_PAY → 입금 대기 메시지")
        void allWaitPay() {
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.WAIT_PAY),
                orderWithStatus(OrderStatus.WAIT_PAY)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금을 기다리는 중이에요");
        }

        @Test
        @DisplayName("모두 WAIT_PAY_CHECK → 입금 확인 대기 메시지")
        void allWaitPayCheck() {
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
                orderWithStatus(OrderStatus.WAIT_PAY_CHECK)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금 확인을 기다리는 참여자가 있어요");
        }

        @Test
        @DisplayName("WAIT_PAY_CHECK + PAID 혼재 → 모두 입금했으므로 입금 확인 대기 메시지")
        void waitPayCheckAndPaid() {
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
                orderWithStatus(OrderStatus.PAID)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금 확인을 기다리는 참여자가 있어요");
        }
    }

    @Nested
    @DisplayName("CLOSED 상태 - 혼재 케이스 (미입금자 존재)")
    class ClosedMixedStatus {

        @Test
        @DisplayName("WAIT_PAY + WAIT_PAY_CHECK 혼재 → 미입금자 있으므로 입금 대기 메시지")
        void waitPayAndWaitPayCheck() {
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.WAIT_PAY),
                orderWithStatus(OrderStatus.WAIT_PAY_CHECK)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금을 기다리는 중이에요");
        }

        @Test
        @DisplayName("WAIT_PAY + WAIT_PAY_CHECK + PAID 혼재 → 미입금자 있으므로 입금 대기 메시지")
        void allThreeMixed() {
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.WAIT_PAY),
                orderWithStatus(OrderStatus.WAIT_PAY_CHECK),
                orderWithStatus(OrderStatus.PAID)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금을 기다리는 중이에요");
        }
    }

    @Nested
    @DisplayName("CLOSED 상태 - 엣지 케이스")
    class ClosedEdgeCase {

        @Test
        @DisplayName("주문 없음 → WAIT_PAY_CHECK도 없으므로 입금 대기 메시지 (방어)")
        void emptyOrders() {
            assertThat(resolveMessage(closedPost(), Collections.emptyList()))
                .isEqualTo("입금을 기다리는 중이에요");
        }

        @Test
        @DisplayName("PAID만 있음 → PAYMENT_DONE 전환 전 과도기 상태, 입금 대기 메시지 반환")
        void onlyPaid() {
            // CLOSED + 전원 PAID는 PAYMENT_DONE 전환 직전 과도기. 메시지 기준은 WAIT_PAY 유무
            List<Order> orders = List.of(
                orderWithStatus(OrderStatus.PAID),
                orderWithStatus(OrderStatus.PAID)
            );
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금을 기다리는 중이에요");
        }

        @Test
        @DisplayName("참여자 1명, WAIT_PAY_CHECK → 입금 확인 대기 메시지")
        void singleWaitPayCheck() {
            List<Order> orders = List.of(orderWithStatus(OrderStatus.WAIT_PAY_CHECK));
            assertThat(resolveMessage(closedPost(), orders))
                .isEqualTo("입금 확인을 기다리는 참여자가 있어요");
        }
    }

    @Nested
    @DisplayName("CLOSED 외 상태 - sellerMessage 그대로 반환")
    class NonClosedStatus {

        private GroupBuyPost postWithStatus(GroupBuyPostStatus status) {
            GroupBuyPost post = mock(GroupBuyPost.class);
            when(post.getStatus()).thenReturn(status);
            return post;
        }

        @Test
        @DisplayName("RECRUITING → 모집중 메시지")
        void recruiting() {
            assertThat(resolveMessage(postWithStatus(GroupBuyPostStatus.RECRUITING), List.of()))
                .isEqualTo("참여자들을 기다리고 있어요");
        }

        @Test
        @DisplayName("PAYMENT_DONE → 입금완료 메시지")
        void paymentDone() {
            assertThat(resolveMessage(postWithStatus(GroupBuyPostStatus.PAYMENT_DONE), List.of()))
                .isEqualTo("배송을 기다리는 참여자가 있어요");
        }

        @Test
        @DisplayName("SHIPPING → 배송시작 메시지")
        void shipping() {
            assertThat(resolveMessage(postWithStatus(GroupBuyPostStatus.SHIPPING), List.of()))
                .isEqualTo("배송을 시작했어요");
        }

        @Test
        @DisplayName("DELIVERED → 거래종료 메시지")
        void delivered() {
            assertThat(resolveMessage(postWithStatus(GroupBuyPostStatus.DELIVERED), List.of()))
                .isEqualTo("거래가 종료되었어요");
        }
    }
}
