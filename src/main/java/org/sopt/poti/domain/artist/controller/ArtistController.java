package org.sopt.poti.domain.artist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistListResponse;
import org.sopt.poti.domain.artist.dto.response.MemberListResponse;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Artists", description = "아티스트 관련 API")
@RequestMapping("/api/v1/artists")
public class ArtistController {

  private final ArtistService artistService;

  @GetMapping
  @Operation(summary = "아티스트 리스트 조회", description = "온보딩 최애 아티스트 선택시 사용되는 아티스트 리스트입니다.")
  public ResponseEntity<ApiResponse<ArtistListResponse>> getArtists() {
    ArtistListResponse data = artistService.getArtists();
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
  }

  @GetMapping("/{artistId}/members")
  @Operation(summary = "아티스트에 해당하는 멤버 조회", description =
      "특정 아티스트(artistId)에 소속된 모든 멤버 리스트를 조회합니다.\n"
          + "\n"
          + "[사용되는 곳]\n"
          + "- 분철 글 등록 화면의 멤버 설정\n"
          + "- 굿즈별 페이지의 멤버 필터링 모달\n"
          + "- 멤버 선택 바텀시트\n"
          + "\n"
          + "[참고]\n"
          + "- 선택 상태(isSelected)는 반환하지 않습니다. 클라이언트에서 로컬 상태와 비교하여 처리해야 합니다.")
  public ResponseEntity<ApiResponse<MemberListResponse>> getMembers(@PathVariable Long artistId) {
    MemberListResponse members = artistService.getMembers(artistId);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, members));
  }
}
