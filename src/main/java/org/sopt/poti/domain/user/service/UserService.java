package org.sopt.poti.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.user.dto.request.UserOnboardingRequest;
import org.sopt.poti.domain.user.dto.response.UserOnboardingResponse;
import org.sopt.poti.domain.user.entity.SellerPostRatingContribution;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.SellerPostRatingContributionRepository;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final ArtistService artistService;
  private final SellerPostRatingContributionRepository contributionRepository;

  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
  }

  @Transactional
  public UserOnboardingResponse saveOnboarding(Long userId, UserOnboardingRequest req) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    user.updateNickname(req.nickname());

    Artist favorite = (req.favoriteArtistId() == null)
        ? null
        : artistService.getById(req.favoriteArtistId());

    user.updateFavoriteArtist(favorite);

    return new UserOnboardingResponse(
        user.getNickname(),
        user.getFavoriteArtist() == null ? null : user.getFavoriteArtist().getId()
    );
  }

  public Optional<User> findUserBySocialIdAndSocialType(String socialId, SocialType socialType) {
    return userRepository.findBySocialIdAndSocialType(socialId, socialType);
  }

  @Transactional
  public void registerUser(User user) {
    userRepository.save(user);
  }

  private double computeAlpha(int postCount, int n) {
    double base;
    double k;

    if (postCount <= 5) {
      base = 0.1;
      k = 0.0;
    } else if (postCount <= 10) {
      base = 0.2;
      k = 0.02;
    } else if (postCount <= 20) {
      base = 0.12;
      k = 0.015;
    } else if (postCount <= 30) {
      base = 0.08;
      k = 0.008;
    } else {
      base = 0.05;
      k = 0.005;
    }

    double alpha = base + k * n;

    double alphaMax = 0.7;
    if (alpha > alphaMax) {
      alpha = alphaMax;
    }

    double alphaMin = 0.05;
    if (alpha != 0.0 && alpha < alphaMin) {
      alpha = alphaMin;
    }

    return alpha;
  }

  @Transactional
  public void applyPostContribution(
      Long sellerId,
      Long postId,
      double postAvg,
      int reviewCount,
      int postCount
  ) {
    // 락 관리

    // 1 판매자 락
    User seller = userRepository.findByIdWithLock(sellerId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    if (reviewCount <= 0) {
      return;
    }

    // 2 contribution 락
    SellerPostRatingContribution contrib = contributionRepository
        .findBySellerIdAndPostIdWithLock(sellerId, postId)
        .orElseGet(() -> contributionRepository.save(
            SellerPostRatingContribution.create(sellerId, postId)));

    // 3 새 weight
    double newWeight = computeAlpha(postCount, reviewCount);

    // 4 이전 기여 → 새 기여 갱신
    double oldWeighted = contrib.getAppliedAvg() * contrib.getAppliedWeight();
    double newWeighted = postAvg * newWeight;

    double deltaWeightedSum = newWeighted - oldWeighted;
    double deltaWeight = newWeight - contrib.getAppliedWeight();

    // 5 seller에 delta 반영
    seller.addRatingWeightedDelta(deltaWeightedSum, deltaWeight);

    // 6 contrib 갱신
    contrib.update(postAvg, newWeight);
  }
}
