package org.sopt.poti.domain.user.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.user.dto.response.OthersProfileResponse;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OthersProfileService {

  private final UserRepository userRepository;
  private final GroupBuyRepository groupBuyRepository;
  private final OrderService orderService;
  private final ActivityMessageResolver activityMessageResolver;

  public OthersProfileResponse getProfile(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    String activityMessage = activityMessageResolver.resolve(user.getLastActiveAt());
    LocalDate joinedAt = user.getCreatedAt().toLocalDate();

    List<OrderStatus> pInProgressStatuses = List.of(
        OrderStatus.WAIT_PAY,
        OrderStatus.WAIT_PAY_CHECK,
        OrderStatus.PAID,
        OrderStatus.SHIPPED
    );
    int pInProgress = orderService.countByUser_IdAndStatusIn(userId, pInProgressStatuses);
    int pCompleted = orderService.countByUser_IdAndStatusIn(userId, List.of(OrderStatus.DELIVERED));

    List<GroupBuyPostStatus> rInProgressStatuses = List.of(
        GroupBuyPostStatus.RECRUITING,
        GroupBuyPostStatus.CLOSED,
        GroupBuyPostStatus.PAYMENT_DONE,
        GroupBuyPostStatus.SHIPPING
    );
    int rInProgress = groupBuyRepository.countByLeader_IdAndStatusIn(userId, rInProgressStatuses);
    int rCompleted = groupBuyRepository.countByLeader_IdAndStatusIn(userId,
        List.of(GroupBuyPostStatus.DELIVERED));

    return new OthersProfileResponse(
        user.getId(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getRatingAvg(),
        activityMessage,
        joinedAt,
        new OthersProfileResponse.Summary(pInProgress, pCompleted),
        new OthersProfileResponse.Summary(rInProgress, rCompleted)
    );
  }
}
