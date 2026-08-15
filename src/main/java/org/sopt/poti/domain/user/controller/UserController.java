package org.sopt.poti.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.request.UpdateFavoriteArtistRequest;
import org.sopt.poti.domain.user.dto.request.UserOnboardingRequest;
import org.sopt.poti.domain.user.dto.response.UserOnboardingResponse;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "유저 관련 API")
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @Operation(summary = "최애 아티스트 변경", description = "마이페이지에서 최애 아티스트를 변경합니다. artistId null 시 최애 없음으로 변경됩니다.")
  @PatchMapping("/me/favorite-artist")
  public ResponseEntity<ApiResponse<Void>> updateFavoriteArtist(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody UpdateFavoriteArtistRequest request
  ) {
    userService.updateFavoriteArtist(userPrincipal.getUserId(), request.artistId());
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, null));
  }

  @Operation(summary = "온보딩", description = "온보딩 정보(닉네임, 최애 설정) 입력")
  @PatchMapping("/onboarding")
  public ResponseEntity<ApiResponse<UserOnboardingResponse>> onboarding(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Validated @RequestBody UserOnboardingRequest request
  ) {
    Long userId = userPrincipal.getUserId();

    UserOnboardingResponse response = userService.saveOnboarding(userId, request);

    return ResponseEntity
        .status(SuccessStatus.CREATED.getHttpStatus())
        .body(ApiResponse.created(SuccessStatus.CREATED, response));
  }
}