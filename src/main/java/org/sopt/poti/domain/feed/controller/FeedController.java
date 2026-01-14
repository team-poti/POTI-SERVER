package org.sopt.poti.domain.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.feed.dto.response.FeedResponse;
import org.sopt.poti.domain.feed.service.FeedService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "피드(분철글 목록) API")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    @Operation(summary = "분철 게시글 목록 조회", description = "피드에서 분철 게시글 목록을 필터링(아티스트), 정렬(최신/인기), 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<FeedResponse>> getFeed(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false, defaultValue = "LATEST") String sort,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FeedResponse response = feedService.getFeed(userPrincipal.getUserId(), artistId, sort, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(SuccessStatus.OK, response));
    }
}
