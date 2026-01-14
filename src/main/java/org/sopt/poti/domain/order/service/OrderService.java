package org.sopt.poti.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public int countByUser_Id(Long userId) {
    return orderRepository.countByUser_Id(userId);
  }

  public int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses) {
    return orderRepository.countByUser_IdAndStatusIn(userId, statuses);
  }

  public Order getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));
  }

  public void validateDelivered(Order order) {
    if (order.getStatus() != OrderStatus.DELIVERED) {
      throw new BusinessException(ErrorStatus.ORDER_NOT_COMPLETED);
    }
  }

  public void validateOrderOwner(Order order, Long writerUserId) {
    if (!order.getUser().getId().equals(writerUserId)) {
      throw new BusinessException(ErrorStatus.REVIEW_FORBIDDEN);
    }
  }

  public List<OrderItem> getOrderItemsByOptionIds(List<Long> optionIds) {
    return orderItemRepository.findAllByGroupBuyOptionIdIn(optionIds);
  }
}