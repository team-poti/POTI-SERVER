package org.sopt.poti.domain.groupbuy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyCreateRequest;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyListRequest;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyMeStatus;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyMeStatus;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyCreateResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyListResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyMeResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyMeResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPostOptionResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyTitlesResponse;
import org.sopt.poti.domain.groupbuy.service.GroupBuyService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "GroupBuy", description = "공동구매글 관련 API")
public class GroupBuyController {

  private final GroupBuyService groupBuyService;
  private final ArtistService artistService;

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
    }
    List<String> titles = groupBuyService.searchTitles(artistId, keyword);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success(SuccessStatus.OK, GroupBuyTitlesResponse.of(titles)));
  }

  @GetMapping("/{postId}")
  @Operation(summary = "분철글 상세 조회", description = "일반 사용자가 분철글에 대한 정보를 상세 조회합니다.")
  public ResponseEntity<ApiResponse<GroupBuyDetailResponse>> getGroupBuyPostDetail(
      @PathVariable Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {

    GroupBuyDetailResponse groupBuyDetail = groupBuyService.getGroupBuyDetail(
        userPrincipal.getUserId(), postId);

    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, groupBuyDetail));
  }

  @GetMapping("/pots")
  @Operation(summary = "상품별 분철 팟 목록 조회", description = "특정 아티스트의 특정 상품명에 해당하는 분철 팟(게시글) 목록을 필터링 및 정렬하여 조회합니다. (남아있는 멤버 필터링, 평점순, 마감임박순 정렬)")
  public ResponseEntity<ApiResponse<GroupBuyListResponse>> getGroupBuyPotList(
      @ModelAttribute @Valid GroupBuyListRequest request,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    GroupBuyListResponse response = groupBuyService.getGroupBuyListByPostTitle(request, pageable);
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @GetMapping("/{postId}/options")
  @Operation(summary = "분철글 선택 가능한 분철 멤버 옵션과 배송 방법 조회", description = "특정 분철글에 참여(구매) 가능한 멤버 옵션과 배송 방법들을 조회합니다.")
  public ResponseEntity<ApiResponse<GroupBuyPostOptionResponse>> getGroupBuyOptionList(
      @PathVariable(name = "postId") Long postId
  ) {
    GroupBuyPostOptionResponse groupBuyPostOptionResponse = groupBuyService.getGroupBuyPostOptionResponse(
        postId);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, groupBuyPostOptionResponse));
  }

  @GetMapping("/artists")
  @Operation(summary = "아티스트 실시간 검색 자동완성/추천", description = "입력 키워드를 기반으로 아티스트명을 추천합니다.")
  public ApiResponse<ArtistTitlesResponse> searchArtists(
      @RequestParam String keyword
  ) {
    return ApiResponse.success(SuccessStatus.OK, artistService.searchArtists(keyword));
  }

  @GetMapping("/me")
  @Operation(summary = "판매자용 내 판매 내역 리스트 조회", description =
      "총대로 진행했던 내역들을 조회합니다. 상태값은 IN_PROGRESS (진행중) \n"
          + "또는\n"
          + "COMPLETED (완료)가 있습니다.")
  public ResponseEntity<ApiResponse<GroupBuyMeResponse>> getGroupBuyMeList(
      @RequestParam(name = "status") GroupBuyMeStatus status,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    GroupBuyMeResponse response = groupBuyService.getMyGroupBuyPosts(userPrincipal.getUserId(), status);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
  }
}
