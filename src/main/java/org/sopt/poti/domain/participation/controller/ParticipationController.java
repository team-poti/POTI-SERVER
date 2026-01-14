package org.sopt.poti.domain.participation.controller;

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
@RequestMapping("/api/v1/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    @GetMapping
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