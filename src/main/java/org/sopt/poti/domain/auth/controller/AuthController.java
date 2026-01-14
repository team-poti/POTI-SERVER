package org.sopt.poti.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.dto.request.AuthRequest;
import org.sopt.poti.domain.auth.dto.request.TokenReissueRequest;
import org.sopt.poti.domain.auth.dto.response.AuthResponse;
import org.sopt.poti.domain.auth.dto.response.TokenReissueResponse;
import org.sopt.poti.domain.auth.service.AuthService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
