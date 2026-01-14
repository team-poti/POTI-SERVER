package org.sopt.poti.domain.participation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.participation.dto.response.ParticipationSummaryResponse;
import org.sopt.poti.domain.participation.entity.ParticipationStatus;
import org.sopt.poti.domain.participation.service.ParticipationService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Participation", description = "공구 참여 관련 API")
@RequestMapping("/api/v1/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    @GetMapping
    @Operation(summary = "내 공구 참여 내역 리스트", description = "로그인한 유저가 구매자로 참여한 공구 참여 내역 리스트를 반환합니다.")
    public ResponseEntity<ApiResponse<ParticipationSummaryResponse>> getMyParticipations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam ParticipationStatus status
    ) {
        Long userId = userPrincipal.getUserId();

        ParticipationSummaryResponse data =
                participationService.getMyParticipations(userId, status);

        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
    }
}