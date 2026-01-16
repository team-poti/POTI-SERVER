package org.sopt.poti.domain.user.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
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
  private final ActivityMessageResolver activityMessageResolver;

  public OthersProfileResponse getProfile(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    String activityMessage = activityMessageResolver.resolve(user.getLastActiveAt());
    LocalDate joinedAt = user.getCreatedAt().toLocalDate();
    boolean hasFavoriteArtist = (user.getFavoriteArtist() != null);

    int rTotal = groupBuyRepository.countByLeader_Id(userId);

    List<GroupBuyPostStatus> rInProgressStatuses = List.of(
        GroupBuyPostStatus.RECRUITING,
        GroupBuyPostStatus.PAYMENT_DONE,
        GroupBuyPostStatus.SHIPPING
    );
    int rInProgress = groupBuyRepository.countByLeader_IdAndStatusIn(userId, rInProgressStatuses);

    List<GroupBuyPostStatus> rCompletedStatuses = List.of(
        GroupBuyPostStatus.CLOSED,
        GroupBuyPostStatus.DELIVERED
    );
    int rCompleted = groupBuyRepository.countByLeader_IdAndStatusIn(userId, rCompletedStatuses);

    OthersProfileResponse.Summary recruit = new OthersProfileResponse.Summary(rTotal, rInProgress,
        rCompleted);

    return new OthersProfileResponse(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getRatingAvg(),
        activityMessage,
        joinedAt,
        hasFavoriteArtist,
        recruit
    );
  }
}
