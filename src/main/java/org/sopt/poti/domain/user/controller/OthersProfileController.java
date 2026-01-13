package org.sopt.poti.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.response.OthersProfileResponse;
import org.sopt.poti.domain.user.service.OthersProfileService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Profile", description = "타인 프로필 관련 API")
public class OthersProfileController {

    private final OthersProfileService othersProfileService;

    @GetMapping("/{userId}/profile")
    @Operation(summary = "타인 프로필을 조회하는 API입니다.", description = "해당 userId의 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<OthersProfileResponse>> getProfile(
            @PathVariable Long userId
    ) {
        OthersProfileResponse data = othersProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
    }
}