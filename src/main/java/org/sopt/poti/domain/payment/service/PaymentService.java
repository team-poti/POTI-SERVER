package org.sopt.poti.domain.payment.service;

import java.time.LocalDateTime;
import java.util.List;
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
import org.sopt.poti.domain.fcmtoken.service.FcmNotificationService;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.payment.repository.PaymentRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final OrderService orderService;
  private final OrderRepository orderRepository;

  @Autowired(required = false)
  private FcmNotificationService fcmNotificationService;

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
        order.getTotalAmount(),
        order,
        req.depositedAt()
    );
    try {
      paymentRepository.saveAndFlush(payment);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorStatus.PAYMENT_ALREADY_SUBMITTED);
    }
    order.requestPayCheck();

    if (fcmNotificationService != null) {
      fcmNotificationService.notifyWaitPayCheck(order.getGroupBuyPost());
    }

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

    LocalDateTime confirmDateTime = LocalDateTime.now();
    // 입금 확인됨으로 처리
    order.confirmPayment();
    OrderConfirmResponse orderConfirmResponse = new OrderConfirmResponse(orderId,
        order.getStatus(), confirmDateTime);

    long unpaidCount = orderService.countByGroupBuyPostIdAndStatusIn(
        groupBuyPost.getId(),
        List.of(OrderStatus.WAIT_PAY, OrderStatus.WAIT_PAY_CHECK)
    );
    if (unpaidCount == 0) { // 모두 입금 완료(PAID) 이상
      groupBuyPost.updateStatus(GroupBuyPostStatus.PAYMENT_DONE);
      if (fcmNotificationService != null) {
        List<Long> participantIds = orderRepository.findUserIdsByGroupBuyPost_Id(groupBuyPost.getId());
        fcmNotificationService.notifyPostStatusChanged(groupBuyPost, participantIds);
      }
    }

    return orderConfirmResponse;
  }
}