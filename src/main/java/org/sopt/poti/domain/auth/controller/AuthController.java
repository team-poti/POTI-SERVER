package org.sopt.poti.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.dto.request.AuthRequest;
import org.sopt.poti.domain.auth.dto.request.LogoutRequest;
import org.sopt.poti.domain.auth.dto.request.TokenReissueRequest;
import org.sopt.poti.domain.auth.dto.request.WithdrawRequest;
import org.sopt.poti.domain.auth.dto.response.AuthResponse;
import org.sopt.poti.domain.auth.dto.response.TokenReissueResponse;
import org.sopt.poti.domain.auth.service.AuthService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "소셜 로그인/회원가입", description = "카카오 소셜 로그인을 수행하고 액세스/리프레시 토큰을 발급합니다.")
  public ResponseEntity<ApiResponse<AuthResponse>> socialLogin(
      @RequestBody @Valid AuthRequest request) {
    AuthResponse response = authService.socialLogin(request);
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @PostMapping("/reissue")
  @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 Access Token 및 Refresh Token을 재발급합니다.")
  public ResponseEntity<ApiResponse<TokenReissueResponse>> reissue(
      @RequestBody @Valid TokenReissueRequest request
  ) {
    TokenReissueResponse response = authService.reissue(request);
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @PostMapping("/logout")
  @Operation(summary = "로그아웃", description = "Access Token을 블랙리스트 처리하고 Refresh Token을 삭제합니다. fcmToken을 보내면 해당 토큰도 삭제합니다.")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader("Authorization") String accessToken,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody(required = false) LogoutRequest request
  ) {
    String fcmToken = (request != null) ? request.fcmToken() : null;
    authService.logout(extractToken(accessToken), userPrincipal.getUserId(), fcmToken);
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK)
    );
  }

  @DeleteMapping("/withdrawal")
  @Operation(summary = "회원탈퇴", description = "회원탈퇴 시 SoftDelete 방향으로 처리되며, email, socialId, nickname 등이 탈퇴처리 됩니다. 진행 중인 거래가 있으면 탈퇴할 수 없습니다.")
  public ResponseEntity<ApiResponse<Void>> withdrawal(
      @RequestHeader("Authorization") String accessToken,
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody(required = false) WithdrawRequest request
  ) {
    String reason = (request != null) ? request.reason() : null;
    authService.withdraw(extractToken(accessToken), userPrincipal.getUserId(), reason);
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK)
    );
  }

  private String extractToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new BusinessException(ErrorStatus.INVALID_TOKEN);
    }
    return authorizationHeader.substring("Bearer ".length());
  }
}
