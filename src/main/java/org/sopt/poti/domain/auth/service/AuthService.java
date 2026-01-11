package org.sopt.poti.domain.auth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.auth.dto.request.AuthRequest;
import org.sopt.poti.domain.auth.dto.response.AuthResponse;
import org.sopt.poti.domain.auth.entity.RefreshToken;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.external.kakao.KakaoFeignClient;
import org.sopt.poti.global.external.kakao.dto.KakaoUserResponse;
import org.sopt.poti.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final KakaoFeignClient kakaoFeignClient; // FeignClient 주입
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  @Transactional
  public AuthResponse socialLogin(AuthRequest request) {
    SocialType socialType = SocialType.valueOf(request.socialType().toUpperCase());

    KakaoUserResponse kakaoUserResponse = getKakaoUserResponse(request.token());
    String socialId = String.valueOf(kakaoUserResponse.getId());

    boolean isNewUser;
    User user;

    Optional<User> existingUser = userRepository.findBySocialIdAndSocialType(socialId, socialType);

    if (existingUser.isPresent()) {
      user = existingUser.get();
      isNewUser = false;
      
    } else {
      KakaoUserResponse.KakaoAccount kakaoAccount = kakaoUserResponse.getKakaoAccount();
      String email = null;
      String nickname = null;
      String profileImageUrl = null;

      if (kakaoAccount != null) {
        email = kakaoAccount.getEmail();
        KakaoUserResponse.Profile profile = kakaoAccount.getProfile();
        if (profile != null) {
          nickname = profile.getNickname();
          profileImageUrl = profile.getProfileImageUrl();
        }
      }

      // 회원가입 (신규 유저)
      user = User.createSocialUser(
          socialId,
          socialType,
          email,
          nickname,
          profileImageUrl,  // 있으면 쓰고 없으면 안쓰기
          null // favoriteArtist는 신규 가입 시점에 null
      );
      userRepository.save(user);
      isNewUser = true;
    }

    user.updateLastActiveAt(); // 최종 로그인 시각 업데이트

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    // Redis에 Refresh Token 저장
    refreshTokenRepository.save(RefreshToken.builder()
        .userId(user.getId())
        .refreshToken(refreshToken)
        .ttl(refreshTokenValidity / 1000) // ms -> s 변환
        .build());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .isNewUser(isNewUser)
        .userId(user.getId())
        .build();
  }

  private KakaoUserResponse getKakaoUserResponse(String kakaoAccessToken) {
    try {
      return kakaoFeignClient.getUserInfo("Bearer " + kakaoAccessToken);
    } catch (Exception e) { // FeignClient 호출 중 발생하는 모든 예외 처리

      log.error("Kakao Login 실패: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.INVALID_TOKEN); // 카카오 토큰 에러 시
    }
  }
}
