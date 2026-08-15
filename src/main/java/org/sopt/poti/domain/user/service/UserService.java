package org.sopt.poti.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.user.dto.request.UserOnboardingRequest;
import org.sopt.poti.domain.user.dto.response.UserOnboardingResponse;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
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

  @Transactional
  public void updateFavoriteArtist(Long userId, Long artistId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    Artist favorite = (artistId == null) ? null : artistService.getById(artistId);
    user.updateFavoriteArtist(favorite);
  }

  public Optional<User> findUserBySocialIdAndSocialType(String socialId, SocialType socialType) {
    return userRepository.findBySocialIdAndSocialType(socialId, socialType);
  }

  @Transactional
  public void registerUser(User user) {
    userRepository.save(user);
  }
}
