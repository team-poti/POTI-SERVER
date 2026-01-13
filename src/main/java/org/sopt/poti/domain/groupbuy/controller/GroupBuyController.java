package org.sopt.poti.domain.groupbuy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyCreateRequest;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyCreateResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyTitlesResponse;
import org.sopt.poti.domain.groupbuy.service.GroupBuyService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groupbuys")
@RequiredArgsConstructor
@Tag(name = "GroupBuy", description = "공동구매 관련 API")
public class GroupBuyController {

  private final GroupBuyService groupBuyService;

  @PostMapping
  @Operation(summary = "공동구매 게시글 등록", description = "새로운 공동구매 게시글을 등록합니다.")
  public ResponseEntity<ApiResponse<GroupBuyCreateResponse>> createGroupBuy(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody @Valid GroupBuyCreateRequest request
  ) {
    GroupBuyCreateResponse response = groupBuyService.createGroupBuyPost(userPrincipal.getUserId(),
        request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(SuccessStatus.CREATED, response));
  }

  @GetMapping("/titles")
  @Operation(summary = "상품명 자동완성/추천", description = "입력 키워드를 기반으로 공동구매 상품명 리스트를 추천합니다.")
      public ResponseEntity<ApiResponse<GroupBuyTitlesResponse>> searchTitles(
              @RequestParam Long artistId,
              @RequestParam String keyword
      ) {
          if (!StringUtils.hasText(keyword)) {
              return ResponseEntity.status(HttpStatus.OK)
                      .body(ApiResponse.success(SuccessStatus.OK,
                              GroupBuyTitlesResponse.of(Collections.emptyList())));
          }    List<String> titles = groupBuyService.searchTitles(artistId, keyword);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success(SuccessStatus.OK, GroupBuyTitlesResponse.of(titles)));
  }
}
