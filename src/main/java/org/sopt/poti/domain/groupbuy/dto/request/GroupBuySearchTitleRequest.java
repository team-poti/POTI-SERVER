package org.sopt.poti.domain.groupbuy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupBuySearchTitleRequest(
    @Schema(description = "아티스트 ID", example = "24")
    @NotNull(message = "아티스트 ID는 필수입니다.")
    Long artistId,

    @Schema(description = "검색 키워드", example = "러브다이브")
    @NotBlank(message = "검색 키워드는 필수입니다.")
    String keyword
) {

  public GroupBuySearchTitleRequest {
    if (keyword != null) {
      keyword = keyword.trim();
    }
  }
}
