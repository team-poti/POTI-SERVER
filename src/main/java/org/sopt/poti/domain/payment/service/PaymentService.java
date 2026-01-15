package org.sopt.poti.domain.payment.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.payment.dto.request.DepositFormRequest;
import org.sopt.poti.domain.payment.dto.response.DepositFormResponse;
import org.sopt.poti.domain.payment.entity.Payment;
import org.sopt.poti.domain.payment.repository.PaymentRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public DepositFormResponse submitDepositForm(Long userId, DepositFormRequest req) {
        Order order = orderService.getOrderById(req.orderId());

        orderService.validateOrderOwner(order, userId);

        if (order.getStatus() != OrderStatus.WAIT_PAY) {
            throw new BusinessException(ErrorStatus.ORDER_NOT_WAIT_PAY);
        }

        @Valid
        Payment payment = paymentRepository.findTopByOrderIdOrderByIdDesc(order.getId())
                .orElseThrow(() -> new BusinessException(ErrorStatus.PAYMENT_NOT_FOUND));

        payment.submitDepositForm(req.depositorName(), req.depositedAt());
        order.requestPayCheck();

        return new DepositFormResponse(payment.getId());
    }
}