package org.sopt.poti.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.dto.request.StartDeliveryRequest;
import org.sopt.poti.domain.delivery.dto.response.StartDeliveryResponse;
import org.sopt.poti.domain.delivery.entity.Delivery;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final DeliveryService deliveryService;

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


  public List<Order> getOrdersByUser(Long userId) {
    return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);
  }

  // 해당 분철글에 대해 OrderStatus가 아닌 주문이 남아있는지 확인
  public long countByGroupBuyPostIdAndStatusNot(Long postId, OrderStatus status) {
    return orderRepository.countUnpaidOrders(postId, status);
  }

  // 배송사 등록
  @Transactional
  public StartDeliveryResponse startOrderDelivery(Long userId, Long orderId,
      StartDeliveryRequest startDeliveryRequest) {
    Order order = orderRepository.findByIdWithPostAndLeader(orderId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

    // 이미 배송 처리된 주문건
    if (deliveryService.existsDeliveryByOrderId(orderId)) {
      throw new BusinessException(ErrorStatus.ORDER_EXSIST_SHIPPINGS);
    }
    
    GroupBuyPost groupBuyPost = order.getGroupBuyPost();
    // 분철글이 본인이 작성한 글인지 확인
    if (!groupBuyPost.getLeader().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }
    LocalDateTime shippedAt = LocalDateTime.now();

    Delivery delivery = Delivery.builder()
        .trackingNumber(startDeliveryRequest.trackingNumber())
        .carrier(startDeliveryRequest.carrier())
        .shippedAt(shippedAt)
        .order(order)
        .build();

    deliveryService.saveDelivery(delivery);
    order.startDelivery();

    return new StartDeliveryResponse(orderId, order.getStatus(),
        startDeliveryRequest.trackingNumber(), shippedAt);
  }
}