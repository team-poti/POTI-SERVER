package org.sopt.poti.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.request.UpdateAddressRequest;
import org.sopt.poti.domain.user.dto.request.UpdateFavoriteArtistRequest;
import org.sopt.poti.domain.user.dto.request.UpdateProfileRequest;
import org.sopt.poti.domain.user.dto.request.UserOnboardingRequest;
import org.sopt.poti.domain.user.dto.response.UserAddressResponse;
import org.sopt.poti.domain.user.dto.response.UserOnboardingResponse;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

  @Operation(summary = "내 배송지 조회", description = "저장된 내 배송지를 조회합니다. 저장된 배송지가 없으면 null을 반환합니다.")
  @GetMapping("/me/address")
  public ResponseEntity<ApiResponse<UserAddressResponse>> getMyAddress(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK,
        userService.getMyAddress(userPrincipal.getUserId())));
  }

  @Operation(summary = "내 배송지 저장/수정", description = "내 배송지를 저장하거나 수정합니다.")
  @PatchMapping("/me/address")
  public ResponseEntity<ApiResponse<?>> updateMyAddress(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody UpdateAddressRequest request
  ) {
    userService.updateMyAddress(userPrincipal.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
  }

  @Operation(summary = "마이페이지 프로필 편집", description = "닉네임과 프로필 사진 URL을 변경합니다. 프로필 사진은 Presigned URL로 S3 업로드 후 URL을 전달하세요.")
  @PatchMapping("/me/profile")
  public ResponseEntity<ApiResponse<?>> updateProfile(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Validated @RequestBody UpdateProfileRequest request
  ) {
    userService.updateProfile(userPrincipal.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
  }

  @Operation(summary = "최애 아티스트 변경", description = "마이페이지에서 최애 아티스트를 변경합니다. artistId null 시 최애 없음으로 변경됩니다.")
  @PatchMapping("/me/favorite-artist")
  public ResponseEntity<ApiResponse<?>> updateFavoriteArtist(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody UpdateFavoriteArtistRequest request
  ) {
    userService.updateFavoriteArtist(userPrincipal.getUserId(), request.artistId());
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
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