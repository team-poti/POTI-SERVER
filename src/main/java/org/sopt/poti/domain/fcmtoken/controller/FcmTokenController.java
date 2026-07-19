package org.sopt.poti.domain.fcmtoken.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.fcmtoken.dto.request.RegisterFcmTokenRequest;
import org.sopt.poti.domain.fcmtoken.service.FcmTokenService;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fcm-tokens")
@RequiredArgsConstructor
@Tag(name = "FCM Token", description = "FCM 토큰 관련 API")
public class FcmTokenController {

  private final FcmTokenService fcmTokenService;
  private final UserService userService;

  @PostMapping
  @Operation(summary = "FCM 토큰 등록/갱신", description = "FCM 토큰을 등록하거나 기존 토큰을 현재 사용자에게 재할당합니다.")
  public ResponseEntity<ApiResponse<Void>> register(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody @Valid RegisterFcmTokenRequest request
  ) {
    fcmTokenService.register(
        userService.getUserById(userPrincipal.getUserId()),
        request.token(),
        request.deviceType()
    );
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
  }

  @DeleteMapping
  @Operation(summary = "FCM 토큰 삭제", description = "특정 FCM 토큰을 삭제합니다.")
  public ResponseEntity<ApiResponse<Void>> delete(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestParam String token
  ) {
    fcmTokenService.deleteByToken(token);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
  }
}
