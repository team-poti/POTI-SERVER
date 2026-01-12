package org.sopt.poti.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.dto.request.AuthRequest;
import org.sopt.poti.domain.auth.dto.response.AuthResponse;
import org.sopt.poti.domain.auth.service.AuthService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK) // 200 OK
    @Operation(summary = "소셜 로그인/회원가입", description = "카카오 소셜 로그인을 수행하고 액세스/리프레시 토큰을 발급합니다.")
    public ApiResponse<AuthResponse> socialLogin(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.socialLogin(request);
        return ApiResponse.success(SuccessStatus.OK, response);
    }
}
