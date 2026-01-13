package org.sopt.poti.domain.user.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.service.GroupBuyService;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.user.dto.response.MyPageResponse;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final GroupBuyService groupBuyService;
    private final ActivityMessageResolver activityMessageResolver;

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

        String activityMessage = activityMessageResolver.resolve(user.getLastActiveAt());
        LocalDate joinedAt = user.getCreatedAt().toLocalDate();
        boolean hasFavoriteArtist = (user.getFavoriteArtist() != null);

        int pTotal = orderService.countByUser_Id(userId);

        List<OrderStatus> pInProgressStatuses = List.of(
                OrderStatus.WAIT_PAY,
                OrderStatus.WAIT_PAY_CHECK,
                OrderStatus.PAID,
                OrderStatus.READY,
                OrderStatus.SHIPPED
        );
        int pInProgress = orderService.countByUser_IdAndStatusIn(userId, pInProgressStatuses);

        List<OrderStatus> pCompletedStatuses = List.of(OrderStatus.DELIVERED);
        int pCompleted = orderService.countByUser_IdAndStatusIn(userId, pCompletedStatuses);

        MyPageResponse.Summary participation = new MyPageResponse.Summary(pTotal, pInProgress, pCompleted);

        int rTotal = groupBuyService.countByLeader_Id(userId);

        List<GroupBuyPostStatus> rInProgressStatuses = List.of(
                GroupBuyPostStatus.RECRUITING,
                GroupBuyPostStatus.PAYMENT_DONE,
                GroupBuyPostStatus.SHIPPING
        );
        int rInProgress = groupBuyService.countByLeader_IdAndStatusIn(userId, rInProgressStatuses);

        List<GroupBuyPostStatus> rCompletedStatuses = List.of(
                GroupBuyPostStatus.CLOSED,
                GroupBuyPostStatus.DELIVERED,
                GroupBuyPostStatus.COMPLETED
        );
        int rCompleted = groupBuyService.countByLeader_IdAndStatusIn(userId, rCompletedStatuses);

        MyPageResponse.Summary recruit = new MyPageResponse.Summary(rTotal, rInProgress, rCompleted);

        return new MyPageResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRatingAvg(),
                activityMessage,
                joinedAt,
                hasFavoriteArtist,
                participation,
                recruit
        );
    }
}
