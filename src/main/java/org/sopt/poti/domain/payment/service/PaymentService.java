package org.sopt.poti.domain.payment.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.payment.dto.request.DepositFormRequest;
import org.sopt.poti.domain.payment.dto.response.DepositFormResponse;
import org.sopt.poti.domain.payment.dto.response.OrderConfirmResponse;
import org.sopt.poti.domain.payment.entity.Payment;
import org.sopt.poti.domain.payment.repository.PaymentRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final OrderService orderService;

  @Transactional
  public DepositFormResponse submitDepositForm(Long userId, DepositFormRequest req) {
    Order order = orderService.getOrderById(req.orderId());
    orderService.validateOrderOwner(order, userId);

    if (order.getStatus() != OrderStatus.WAIT_PAY) {
      throw new BusinessException(ErrorStatus.ORDER_NOT_WAIT_PAY);
    }

    if (paymentRepository.existsByOrder_Id(order.getId())) {
      throw new BusinessException(ErrorStatus.PAYMENT_ALREADY_SUBMITTED);
    }

    Payment payment = Payment.create(
        req.depositorName(),
        req.depositedAt(),
        order.getTotalAmount(),
        order
    );
    try {
      paymentRepository.saveAndFlush(payment);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorStatus.PAYMENT_ALREADY_SUBMITTED);
    }
    order.requestPayCheck();

    return new DepositFormResponse(payment.getId());
  }


  @Transactional
  public OrderConfirmResponse confirmPayment(Long userId, Long orderId) {
    Order order = orderService.getOrderById(orderId);
    GroupBuyPost groupBuyPost = order.getGroupBuyPost();

    // 분철글이 본인이 작성한 글인지 확인
    if (!groupBuyPost.getLeader().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    // 입금 확인됨으로 처리
    order.confirmPayment();
    OrderConfirmResponse orderConfirmResponse = new OrderConfirmResponse(orderId,
        order.getStatus(), LocalDateTime.now());

    // 해당 분철글에 대해 'PAID'가 아닌 주문이 남아있는지 확인
    long allOrderPaid = orderService.countByGroupBuyPostIdAndStatusNot(groupBuyPost.getId(),
        OrderStatus.PAID);

    //  미입금 주문이 없다면(모두 PAID라면) 분철글 상태 변경
    if (!(allOrderPaid > 0)) {
      groupBuyPost.updateStatus(GroupBuyPostStatus.PAYMENT_DONE);
    }

    return orderConfirmResponse;
  }
}