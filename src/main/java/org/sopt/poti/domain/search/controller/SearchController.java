package org.sopt.poti.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.search.dto.response.SearchResponse;
import org.sopt.poti.domain.search.dto.response.SearchSuggestionResponse;
import org.sopt.poti.domain.search.service.SearchService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 API")
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/suggestions")
  @Operation(summary = "검색 자동완성", description = "키워드로 아티스트명과 분철글 제목을 통합 자동완성합니다.")
  public ResponseEntity<ApiResponse<SearchSuggestionResponse>> getSuggestions(
      @RequestParam String keyword
  ) {
    return ResponseEntity.ok(ApiResponse.success(
        SuccessStatus.OK,
        searchService.getSuggestions(keyword)
    ));
  }

  @GetMapping
  @Operation(summary = "검색 결과 조회", description = "키워드로 분철글을 검색합니다. 아티스트명 또는 분철글 제목으로 검색됩니다.")
  public ResponseEntity<ApiResponse<SearchResponse>> search(
      @RequestParam String keyword,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(
        SuccessStatus.OK,
        SearchResponse.from(searchService.search(keyword, pageable))
    ));
  }
}
