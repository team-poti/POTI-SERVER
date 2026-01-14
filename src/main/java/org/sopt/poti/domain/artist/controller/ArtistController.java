package org.sopt.poti.domain.artist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistListResponse;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
