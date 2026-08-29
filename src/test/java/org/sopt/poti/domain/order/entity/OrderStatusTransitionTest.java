package org.sopt.poti.domain.order.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.error.BusinessException;

/**
 * 확정된 흐름: WAIT_PAY → WAIT_PAY_CHECK → PAID → SHIPPED → DELIVERED
 * RECRUITING, READY 제거 후 전환 로직 및 단계 건너뜀 방어 검증
 */
@DisplayName("OrderStatus 상태 전환")
class OrderStatusTransitionTest {

    private Order createOrder() {
        return Order.create(
            mock(GroupBuyPost.class),
            mock(User.class),
            mock(DeliveryMethod.class),
            10000,
            new DeliveryInfo("홍길동", "12345", "서울시 강남구", null, "010-1234-5678"),
            "POTI-001"
        );
    }

    @Test
    @DisplayName("Order 생성 시 초기 상태는 WAIT_PAY")
    void initialStatusIsWaitPay() {
        assertThat(createOrder().getStatus()).isEqualTo(OrderStatus.WAIT_PAY);
    }

    @Nested
    @DisplayName("정상 전환 흐름")
    class HappyPath {

        @Test
        @DisplayName("WAIT_PAY → WAIT_PAY_CHECK")
        void waitPayToWaitPayCheck() {
            Order order = createOrder();
            order.requestPayCheck();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAIT_PAY_CHECK);
        }

        @Test
        @DisplayName("WAIT_PAY_CHECK → PAID")
        void waitPayCheckToPaid() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        @DisplayName("PAID → SHIPPED")
        void paidToShipped() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            order.startDelivery();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("SHIPPED → DELIVERED")
        void shippedToDelivered() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            order.startDelivery();
            order.completeDelivery();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("잘못된 단계 건너뜀 - 예외 발생")
    class InvalidTransition {

        @Test
        @DisplayName("WAIT_PAY → startDelivery 직접 호출 (PAID 건너뜀)")
        void waitPayToStartDeliveryThrows() {
            Order order = createOrder();
            assertThatThrownBy(order::startDelivery)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("WAIT_PAY → confirmPayment 직접 호출 (WAIT_PAY_CHECK 건너뜀)")
        void waitPayToConfirmPaymentThrows() {
            Order order = createOrder();
            assertThatThrownBy(order::confirmPayment)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("WAIT_PAY_CHECK → startDelivery 직접 호출 (PAID 건너뜀)")
        void waitPayCheckToStartDeliveryThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            assertThatThrownBy(order::startDelivery)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("PAID → completeDelivery 직접 호출 (SHIPPED 건너뜀)")
        void paidToCompleteDeliveryThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            assertThatThrownBy(order::completeDelivery)
                .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("같은 단계 재호출 - 예외 발생")
    class DuplicateTransition {

        @Test
        @DisplayName("requestPayCheck 중복 호출 (WAIT_PAY_CHECK → requestPayCheck)")
        void duplicateRequestPayCheckThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            assertThatThrownBy(order::requestPayCheck)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("confirmPayment 중복 호출 (PAID → confirmPayment)")
        void duplicateConfirmPaymentThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            assertThatThrownBy(order::confirmPayment)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("startDelivery 중복 호출 (SHIPPED → startDelivery)")
        void duplicateStartDeliveryThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            order.startDelivery();
            assertThatThrownBy(order::startDelivery)
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("completeDelivery 중복 호출 (DELIVERED → completeDelivery)")
        void duplicateCompleteDeliveryThrows() {
            Order order = createOrder();
            order.requestPayCheck();
            order.confirmPayment();
            order.startDelivery();
            order.completeDelivery();
            assertThatThrownBy(order::completeDelivery)
                .isInstanceOf(BusinessException.class);
        }
    }
}
