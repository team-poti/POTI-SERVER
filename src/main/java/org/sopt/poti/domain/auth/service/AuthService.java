package org.sopt.poti.domain.auth.service;

import feign.FeignException;
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
  private final KakaoFeignClient kakaoFeignClient;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  @Transactional
  public AuthResponse socialLogin(AuthRequest request) {
    if (request.socialType() != SocialType.KAKAO) {
      throw new BusinessException(ErrorStatus.INVALID_SOCIAL_TYPE);
    }

    KakaoUserResponse kakaoUserResponse = getKakaoUserResponse(request.token());
    String socialId = String.valueOf(kakaoUserResponse.getId());

    boolean isNewUser;
    User user;

    Optional<User> existingUser = userRepository.findBySocialIdAndSocialType(socialId,
        request.socialType());

    if (existingUser.isPresent()) {
      user = existingUser.get();
      // 기존 유저이지만, 닉네임이 없으면 온보딩이 필요함
      isNewUser = (user.getNickname() == null);
      // TODO: 기존 유저의 정보(닉네임, 프로필 이미지 등)가 변경되었다면 업데이트 로직 추가
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
          request.socialType(),
          email,
          nickname,
          profileImageUrl,
          null
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
        .ttl(refreshTokenValidity / 1000)
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
    } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
      log.warn("Kakao 토큰 인증 실패: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.INVALID_TOKEN);
    } catch (FeignException e) {
      log.error("Kakao API 호출 실패 (status={}): {}", e.status(), e.getMessage());
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (Exception e) {
      log.error("Kakao Login 내부 오류: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
