package org.sopt.poti.domain.groupbuy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GroupBuyListRequest(
    @NotBlank(message = "상품명(title)은 필수입니다.")
    String title,

    @NotNull(message = "아티스트 ID는 필수입니다.")
    Long artistId,

    List<Long> memberIds, // 필터링할 멤버 ID 리스트 (없으면 null)

    String sort // LATEST, DEADLINE, RATING
) {

  public GroupBuyListRequest {
    if (title != null) {
      title = title.trim(); // 여기서 앞뒤 공백 제거
    }
  }
}
