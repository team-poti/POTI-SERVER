package org.sopt.poti.domain.participation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.participation.dto.response.ParticipationListResponse;
import org.sopt.poti.domain.participation.dto.response.ParticipationSummaryResponse;
import org.sopt.poti.domain.participation.entity.ParticipationStatus;
import org.sopt.poti.domain.user.service.UserService;
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

    return new ParticipationSummaryResponse(inProgressCount, completedCount, filtered);
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

    return new ParticipationListResponse(
        order.getId(),
        post.getId(),
        post.getArtist().getName(),
        post.getTitle(),
        post.getRepresentativeImageUrl(),
        mapToClientStatus(post.getStatus()),
        order.getTotalAmount(),
        order.getCreatedAt()
    );
  }

  private String mapToClientStatus(GroupBuyPostStatus postStatus) {
    return isCompleted(postStatus) ? "COMPLETED" : "IN_PROGRESS";
  }
}