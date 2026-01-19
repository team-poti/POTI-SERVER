package org.sopt.poti.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.request.NicknameDuplicateRequest;
import org.sopt.poti.domain.user.dto.response.NicknameDuplicateResponse;
import org.sopt.poti.domain.user.service.UserNicknameService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "유저 관련 API")
@RequestMapping("/api/v1/users")
public class UserNicknameController {

  private final UserNicknameService userNicknameService;

  @Operation(summary = "닉네임 검사", description = "온보딩시 닉네임 중복확인 및 비속어 검사를 진행합니다.")
  @PostMapping("/nickname/duplicate")
  public ResponseEntity<ApiResponse<NicknameDuplicateResponse>> checkNicknameDuplicate(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Valid @RequestBody NicknameDuplicateRequest request
  ) {
    NicknameDuplicateResponse data = userNicknameService.checkDuplicate(request);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, data)
    );
  }
}