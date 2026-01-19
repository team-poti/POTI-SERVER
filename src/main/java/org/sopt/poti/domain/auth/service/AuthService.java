package org.sopt.poti.domain.auth.service;

import feign.FeignException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.auth.dto.request.AuthRequest;
import org.sopt.poti.domain.auth.dto.request.TokenReissueRequest;
import org.sopt.poti.domain.auth.dto.response.AuthResponse;
import org.sopt.poti.domain.auth.dto.response.TokenReissueResponse;
import org.sopt.poti.domain.auth.entity.RefreshToken;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.entity.UserStatus;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.external.kakao.KakaoFeignClient;
import org.sopt.poti.global.external.kakao.dto.KakaoUserResponse;
import org.sopt.poti.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final KakaoFeignClient kakaoFeignClient;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RedisTemplate<String, String> redisTemplate; // RedisTemplate 주입

  private final static String DEFAULT_PROFILE_IMAGE = "https://poti-s3-bucket.s3.ap-northeast-2.amazonaws.com/users/img-basic-profile.png";

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

    Optional<User> existingUser = userService.findUserBySocialIdAndSocialType(socialId,
        request.socialType());

    if (existingUser.isPresent()) {
      user = existingUser.get();
      // 탈퇴한 유저인지 확인
      if (user.getStatus() == UserStatus.WITHDRAWN) {
        throw new BusinessException(ErrorStatus.USER_NOT_FOUND);
      }
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

      user = User.createSocialUser(
          socialId,
          request.socialType(),
          email,
          nickname,
          (profileImageUrl == null || profileImageUrl.isBlank())
              ? DEFAULT_PROFILE_IMAGE
              : profileImageUrl,
          null
      );
      userService.registerUser(user);
      isNewUser = true;
    }

    user.updateLastActiveAt();

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

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

  @Transactional
  public TokenReissueResponse reissue(TokenReissueRequest request) {
    String oldRefreshToken = request.refreshToken();
    jwtTokenProvider.validateToken(oldRefreshToken);

    Long userId = jwtTokenProvider.getUserIdFromToken(oldRefreshToken);

    RefreshToken storedRefreshToken = refreshTokenRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.INVALID_TOKEN));

    if (!storedRefreshToken.getRefreshToken().equals(oldRefreshToken)) {
      throw new BusinessException(ErrorStatus.INVALID_TOKEN);
    }

    String newAccessToken = jwtTokenProvider.createAccessToken(userId);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

    refreshTokenRepository.delete(storedRefreshToken);
    refreshTokenRepository.save(RefreshToken.builder()
        .userId(userId)
        .refreshToken(newRefreshToken)
        .ttl(refreshTokenValidity / 1000)
        .build());

    return TokenReissueResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  @Transactional
  public void logout(String accessToken, Long userId) {
    Long expiration = jwtTokenProvider.getExpiration(accessToken);
    if (expiration > 0) {
      redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }
    refreshTokenRepository.deleteById(userId);
  }

  @Transactional
  public void withdraw(String accessToken, Long userId) {
    User user = userService.getUserById(userId);
    user.withdraw(); // 유저 상태 변경, 개인 정보 마스킹, deletedAt 설정

    // 로그아웃 처리 (refresh token 삭제, access token 블랙리스트 추가)
    logout(accessToken, userId);
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
