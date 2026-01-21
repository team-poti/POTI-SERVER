package org.sopt.poti.domain.artist.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ArtistSearchRequest(
    @Schema(description = "검색 키워드", example = "엔시티")
    @NotBlank(message = "검색 키워드는 필수입니다.")
    String keyword
) {

  public ArtistSearchRequest {
    if (keyword != null) {
      keyword = keyword.trim();
    }
  }
}