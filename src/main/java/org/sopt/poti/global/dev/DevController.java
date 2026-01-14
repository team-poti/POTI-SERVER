package org.sopt.poti.global.dev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.entity.RefreshToken;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.sopt.poti.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Tag(name = "Dev", description = "개발자 테스트용 API")
public class DevController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final DevService devService;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  @GetMapping("/login")
  @Operation(summary = "개발자용 토큰 발급 (userId=1)", description = "개발 테스트를 위해 1번 유저의 토큰을 즉시 발급합니다. (로컬/Dev 환경 전용)")
  public ResponseEntity<ApiResponse<DevTokenResponseDto>> devLogin() {
    Long devUserId = 1L;

    User user = userService.getUserById(devUserId);

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    refreshTokenRepository.save(RefreshToken.builder()
        .userId(user.getId())
        .refreshToken(refreshToken)
        .ttl(refreshTokenValidity / 1000)
        .build());

    DevTokenResponseDto responseDto = DevTokenResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success(SuccessStatus.OK, responseDto));
  }

  @DeleteMapping("/users/me")
  @Operation(summary = "본인 데이터 완전 삭제 (Hard Delete)", description = "개발 테스트용으로 본인 계정과 관련된 모든 데이터(게시글, 주문 등)를 DB에서 완전히 삭제합니다.")
  public ResponseEntity<ApiResponse<Void>> hardDeleteMe(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    devService.hardDeleteUser(userPrincipal.getUserId());
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK)
    );
  }
}