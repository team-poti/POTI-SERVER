package org.sopt.poti.domain.user.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

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