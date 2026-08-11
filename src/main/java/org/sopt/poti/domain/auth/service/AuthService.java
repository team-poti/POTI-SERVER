package org.sopt.poti.domain.auth.service;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import java.util.List;
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
import org.sopt.poti.domain.fcmtoken.service.FcmTokenService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.entity.UserStatus;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.external.apple.AppleIdTokenValidator;
import org.sopt.poti.global.external.google.GoogleTokenFeignClient;
import org.sopt.poti.global.external.google.dto.GoogleTokenInfoResponse;
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
  private final GoogleTokenFeignClient googleTokenFeignClient;
  private final AppleIdTokenValidator appleIdTokenValidator;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final FcmTokenService fcmTokenService;
  private final OrderService orderService;
  private final GroupBuyRepository groupBuyRepository;

  private final static String DEFAULT_PROFILE_IMAGE = "https://poti-s3-bucket.s3.ap-northeast-2.amazonaws.com/users/img-basic-profile.png";

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  @Value("${google.client-id-ios}")
  private String googleClientIdIos;

  @Value("${google.client-id-android}")
  private String googleClientIdAndroid;

  @Transactional
  public AuthResponse socialLogin(AuthRequest request) {
    SocialUserInfo socialUserInfo = switch (request.socialType()) {
      case KAKAO -> getKakaoUserInfo(request.token());
      case GOOGLE -> getGoogleUserInfo(request.token());
      case APPLE -> getAppleUserInfo(request.token(), request.name());
    };

    boolean isNewUser;
    User user;

    Optional<User> existingUser = userService.findUserBySocialIdAndSocialType(
        socialUserInfo.socialId(), request.socialType());

    if (existingUser.isPresent()) {
      user = existingUser.get();
      if (user.getStatus() == UserStatus.WITHDRAWN) {
        throw new BusinessException(ErrorStatus.USER_NOT_FOUND);
      }
      if (user.getStatus() == UserStatus.SUSPENDED) {
        throw new BusinessException(ErrorStatus.USER_SUSPENDED);
      }
      isNewUser = (user.getNickname() == null);
    } else {
      user = User.createSocialUser(
          socialUserInfo.socialId(),
          request.socialType(),
          socialUserInfo.email(),
          socialUserInfo.nickname(),
          (socialUserInfo.profileImageUrl() == null || socialUserInfo.profileImageUrl().isBlank())
              ? DEFAULT_PROFILE_IMAGE
              : socialUserInfo.profileImageUrl(),
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

  private record SocialUserInfo(String socialId, String email, String nickname, String profileImageUrl) {}

  private SocialUserInfo getKakaoUserInfo(String token) {
    KakaoUserResponse response = getKakaoUserResponse(token);
    String socialId = String.valueOf(response.getId());
    String email = null;
    String nickname = null;
    String profileImageUrl = null;

    KakaoUserResponse.KakaoAccount account = response.getKakaoAccount();
    if (account != null) {
      email = account.getEmail();
      KakaoUserResponse.Profile profile = account.getProfile();
      if (profile != null) {
        nickname = profile.getNickname();
        profileImageUrl = profile.getProfileImageUrl();
      }
    }
    return new SocialUserInfo(socialId, email, nickname, profileImageUrl);
  }

  private SocialUserInfo getGoogleUserInfo(String idToken) {
    try {
      GoogleTokenInfoResponse response = googleTokenFeignClient.getTokenInfo(idToken);
      String aud = response.getAud();
      if (!googleClientIdIos.equals(aud) && !googleClientIdAndroid.equals(aud)) {
        throw new BusinessException(ErrorStatus.INVALID_TOKEN);
      }
      return new SocialUserInfo(response.getSub(), response.getEmail(), null, null);
    } catch (BusinessException e) {
      throw e;
    } catch (FeignException.BadRequest | FeignException.Unauthorized | FeignException.Forbidden e) {
      log.warn("Google ID Token 검증 실패 (status={}): {}", e.status(), e.getMessage());
      throw new BusinessException(ErrorStatus.INVALID_TOKEN);
    } catch (FeignException e) {
      log.error("Google API 호출 실패 (status={}): {}", e.status(), e.getMessage());
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (Exception e) {
      log.error("Google Login 내부 오류: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private SocialUserInfo getAppleUserInfo(String idToken, String name) {
    Claims claims = appleIdTokenValidator.validate(idToken);
    String socialId = claims.getSubject();
    String email = claims.get("email", String.class);
    return new SocialUserInfo(socialId, email, name, null);
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
  public void logout(String accessToken, Long userId, String fcmToken) {
    Long expiration = jwtTokenProvider.getExpiration(accessToken);
    if (expiration > 0) {
      redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }
    refreshTokenRepository.deleteById(userId);
    if (fcmToken != null && !fcmToken.isBlank()) {
      fcmTokenService.deleteByToken(userId, fcmToken);
    }
  }

  @Transactional
  public void withdraw(String accessToken, Long userId, String reason) {
    validateNoActiveTransaction(userId);

    User user = userService.getUserById(userId);
    user.withdraw(reason);

    fcmTokenService.deleteAllByUserId(userId);
    logout(accessToken, userId, null);
  }

  private void validateNoActiveTransaction(Long userId) {
    List<OrderStatus> activeOrderStatuses = List.of(
        OrderStatus.WAIT_PAY, OrderStatus.WAIT_PAY_CHECK, OrderStatus.PAID,
        OrderStatus.SHIPPED
    );
    if (orderService.countByUser_IdAndStatusIn(userId, activeOrderStatuses) > 0) {
      throw new BusinessException(ErrorStatus.ACTIVE_TRANSACTION_EXISTS);
    }

    List<GroupBuyPostStatus> activePostStatuses = List.of(
        GroupBuyPostStatus.RECRUITING, GroupBuyPostStatus.CLOSED,
        GroupBuyPostStatus.PAYMENT_DONE, GroupBuyPostStatus.SHIPPING
    );
    if (groupBuyRepository.countByLeader_IdAndStatusIn(userId, activePostStatuses) > 0) {
      throw new BusinessException(ErrorStatus.ACTIVE_TRANSACTION_EXISTS);
    }
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
