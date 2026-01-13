package org.sopt.poti.domain.user.controller;

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
public class OthersProfileController {

    private final OthersProfileService othersProfileService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<OthersProfileResponse>> getProfile(
            @PathVariable Long userId
    ) {
        OthersProfileResponse data = othersProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
    }
}