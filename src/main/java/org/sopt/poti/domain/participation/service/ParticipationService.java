package org.sopt.poti.domain.participation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.participation.dto.response.LeaderUserIdResponse;
import org.sopt.poti.domain.participation.dto.response.ParticipationListResponse;
import org.sopt.poti.domain.participation.dto.response.ParticipationSummaryResponse;
import org.sopt.poti.domain.participation.entity.ParticipationStatus;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

  private final OrderService orderService;
  private final UserService userService;

  public ParticipationSummaryResponse getMyParticipations(Long userId, ParticipationStatus status) {

    userService.getUserById(userId);

    List<Order> orders = orderService.getOrdersByUser(userId);

    int inProgressCount = 0;
    int completedCount = 0;

    for (Order order : orders) {
      GroupBuyPostStatus postStatus = order.getGroupBuyPost().getStatus();
      if (isCompleted(postStatus)) {
        completedCount++;
      } else {
        inProgressCount++;
      }
    }

    List<ParticipationListResponse> filtered = orders.stream()
        .filter(order -> matchParticipationStatus(order, status))
        .map(this::toResponse)
        .toList();

    return new ParticipationSummaryResponse(status, inProgressCount, completedCount, filtered);
  }

  //배송 완료 처리
  @Transactional
  public LeaderUserIdResponse confirmDelivered(Long userId, Long participationId) {

    Order order = orderService.findWithDetailsById(participationId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

    if (!order.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    // OrderStatus기준 배송중(SHIPPED)일 때만 배송완료 확정 가능
    if (order.getStatus() != OrderStatus.SHIPPED) {
      throw new BusinessException(
          ErrorStatus.ORDER_NOT_SHIPPED);
    }

    // 1 내 주문 배송완료 처리 (OrderStatus)
    order.completeDelivery();

    // 2 해당 공구글의 모든 주문(OrderStatus)이 배송완료인지 검사
    Long postId = order.getGroupBuyPost().getId();
    Long leaderUserId = order.getGroupBuyPost().getLeader().getId();
    long remaining = orderService.countByGroupBuyPostIdAndStatusNot(postId, OrderStatus.DELIVERED);

    // 3) 남은 주문이 0개 -> GroupBuyPostStatus도 배송완료로 변경
    if (remaining == 0) {
      GroupBuyPost post = order.getGroupBuyPost();

      // 이미 DELIVERED면 종료처리
      if (post.getStatus() != GroupBuyPostStatus.DELIVERED) {
        post.completePostDelivery();
      }
    }

    return new LeaderUserIdResponse(leaderUserId);
  }


  private boolean isCompleted(GroupBuyPostStatus status) {
    return status == GroupBuyPostStatus.DELIVERED;
  }

  private boolean matchParticipationStatus(Order order, ParticipationStatus status) {
    GroupBuyPostStatus postStatus = order.getGroupBuyPost().getStatus();

    return switch (status) {
      case IN_PROGRESS -> postStatus == GroupBuyPostStatus.RECRUITING
          || postStatus == GroupBuyPostStatus.CLOSED
          || postStatus == GroupBuyPostStatus.PAYMENT_DONE
          || postStatus == GroupBuyPostStatus.SHIPPING;

      case COMPLETED -> postStatus == GroupBuyPostStatus.DELIVERED;
    };
  }

  private ParticipationListResponse toResponse(Order order) {
    GroupBuyPost post = order.getGroupBuyPost();
    GroupBuyPostStatus postStatus = post.getStatus();

    return new ParticipationListResponse(
        order.getId(),
        post.getId(),
        post.getArtist().getName(),
        post.getTitle(),
        post.getRepresentativeImageUrl(),
        postStatus.name()
    );
  }

  private String mapToClientStatus(GroupBuyPostStatus postStatus) {
    return isCompleted(postStatus) ? "COMPLETED" : "IN_PROGRESS";
  }
}