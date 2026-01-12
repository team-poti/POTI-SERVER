package org.sopt.poti.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.response.MyPageResponse;
import org.sopt.poti.domain.user.service.MyPageService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        MyPageResponse data = myPageService.getMyPage(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
    }
}
