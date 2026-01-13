package org.sopt.poti.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.home.dto.response.HomeResponse;
import org.sopt.poti.domain.home.service.HomeService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "홈 화면 API")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "홈 화면 조회", description = "사용자의 닉네임, 최애 아티스트 공구, 다른 아티스트 공구, 배너 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        HomeResponse response = homeService.getHomeData(userPrincipal.getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(SuccessStatus.OK, response));
    }
}
