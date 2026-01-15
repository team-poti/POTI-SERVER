package org.sopt.poti.domain.groupbuy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GroupBuyListRequest(
    @Schema(description = "분철글 제목 (상품명)", example = "NewJeans How Sweet")
    @NotBlank(message = "상품명(title)은 필수입니다.")
    String title,

    @Schema(description = "아티스트 식별자", example = "1")
    @NotNull(message = "아티스트 ID는 필수입니다.")
    Long artistId,

    @Schema(description = "남아있는 멤버들 필터링 리스트 (선택 사항)", example = "[1, 2]")
    List<Long> memberIds, // 필터링할 멤버 ID 리스트 (없으면 null)

    @Schema(description = "정렬 조건 (LATEST, DEADLINE, RATING)", example = "LATEST")
    String sort // LATEST, DEADLINE, RATING
) {

  public GroupBuyListRequest {
    if (title != null) {
      title = title.trim(); // 여기서 앞뒤 공백 제거
    }
  }
}
