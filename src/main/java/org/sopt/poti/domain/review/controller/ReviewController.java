package org.sopt.poti.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.review.dto.request.ReviewRequest;
import org.sopt.poti.domain.review.dto.response.ReviewResponse;
import org.sopt.poti.domain.review.service.ReviewService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "리뷰 관련 API")
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성(별점 작성)", description = "로그인 한 유저가 거래가 완료된 주문에 대해 별점을 줍니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long reviewId = reviewService.createReview(userPrincipal.getUserId(), request);
        return ResponseEntity
                .status(SuccessStatus.CREATED.getHttpStatus())
                .body(ApiResponse.created(SuccessStatus.CREATED, new ReviewResponse(reviewId)));
    }
}