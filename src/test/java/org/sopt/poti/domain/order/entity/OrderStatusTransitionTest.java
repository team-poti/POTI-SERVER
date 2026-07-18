package org.sopt.poti.domain.order.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.error.BusinessException;

/**
 * OrderStatus 상태 전환 단위 테스트
 *
 * 확정된 흐름: WAIT_PAY → WAIT_PAY_CHECK → PAID → SHIPPED → DELIVERED
 * RECRUITING, READY 제거 후 전환 로직 검증
 */
@DisplayName("OrderStatus 상태 전환")
class OrderStatusTransitionTest {

    private Order createOrder() {
        return Order.create(
            mock(GroupBuyPost.class),
            mock(User.class),
            mock(DeliveryMethod.class),
            10000,
            new DeliveryInfo("홍길동", "12345", "서울시 강남구", "010-1234-5678"),
            "POTI-001"
        );
    }

    @Test
    @DisplayName("Order 생성 시 초기 상태는 WAIT_PAY")
    void initialStatusIsWaitPay() {
        Order order = createOrder();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAIT_PAY);
    }

    @Test
    @DisplayName("WAIT_PAY → WAIT_PAY_CHECK 전환 성공")
    void waitPayToWaitPayCheck() {
        Order order = createOrder();
        order.requestPayCheck();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAIT_PAY_CHECK);
    }

    @Test
    @DisplayName("WAIT_PAY_CHECK → PAID 전환 성공")
    void waitPayCheckToPaid() {
        Order order = createOrder();
        order.requestPayCheck();
        order.confirmPayment();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("PAID → SHIPPED 전환 성공")
    void paidToShipped() {
        Order order = createOrder();
        order.requestPayCheck();
        order.confirmPayment();
        order.startDelivery();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("SHIPPED → DELIVERED 전환 성공")
    void shippedToDelivered() {
        Order order = createOrder();
        order.requestPayCheck();
        order.confirmPayment();
        order.startDelivery();
        order.completeDelivery();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("WAIT_PAY 상태에서 startDelivery 호출 시 예외 발생")
    void startDeliveryFromWaitPayThrows() {
        Order order = createOrder();
        assertThatThrownBy(order::startDelivery)
            .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("WAIT_PAY_CHECK 상태에서 startDelivery 호출 시 예외 발생")
    void startDeliveryFromWaitPayCheckThrows() {
        Order order = createOrder();
        order.requestPayCheck();
        assertThatThrownBy(order::startDelivery)
            .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("WAIT_PAY 상태에서 confirmPayment 호출 시 예외 발생")
    void confirmPaymentFromWaitPayThrows() {
        Order order = createOrder();
        assertThatThrownBy(order::confirmPayment)
            .isInstanceOf(BusinessException.class);
    }
}
