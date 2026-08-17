package org.sopt.poti.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.notification.dto.request.NotificationSettingRequest;
import org.sopt.poti.domain.notification.dto.response.NotificationListResponse;
import org.sopt.poti.domain.notification.dto.response.NotificationSettingResponse;
import org.sopt.poti.domain.notification.service.NotificationService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/notifications")
  @Operation(summary = "알림 내역 조회", description = "내 알림 내역을 최신순으로 페이징 조회합니다.")
  public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(
        SuccessStatus.OK,
        NotificationListResponse.from(notificationService.getNotifications(userPrincipal.getUserId(), pageable))
    ));
  }

  @GetMapping("/notifications/settings")
  @Operation(summary = "알림 설정 조회", description = "현재 알림 수신 설정을 조회합니다.")
  public ResponseEntity<ApiResponse<NotificationSettingResponse>> getSettings(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    return ResponseEntity.ok(ApiResponse.success(
        SuccessStatus.OK,
        notificationService.getSettings(userPrincipal.getUserId())
    ));
  }

  @PatchMapping("/notifications/settings")
  @Operation(summary = "알림 설정 변경", description = "거래 알림 / 이벤트 알림 수신 여부를 설정합니다.")
  public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateSettings(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody NotificationSettingRequest request
  ) {
    return ResponseEntity.ok(ApiResponse.success(
        SuccessStatus.OK,
        notificationService.updateSettings(userPrincipal.getUserId(), request)
    ));
  }
}
