package org.sopt.poti.domain.groupbuy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record GroupBuyPostOptionResponse(
    @Schema(description = "선택 가능한 멤버 옵션 리스트")
    List<GroupBuyPostMemberOption> members,
    @Schema(description = "선택 가능한 배송 옵션 리스트")
    List<GroupBuyPostDeliveryOption> options
) {

  public record GroupBuyPostMemberOption(
      @Schema(description = "멤버 옵션 ID (주문 시 사용)", example = "1")
      Long memberId,
      @Schema(description = "멤버 이름", example = "하니")
      String memberName,
      @Schema(description = "옵션 가격", example = "15000")
      Integer memberPrice
  ) {

  }

  public record GroupBuyPostDeliveryOption(
      @Schema(description = "배송 옵션 ID (주문 시 사용)", example = "10")
      Long deliveryOptionId,
      @Schema(description = "배송 방법명", example = "일반 택배")
      String deliveryName,
      @Schema(description = "배송비", example = "3000")
      Integer deliveryOptionPrice
  ) {

  }
}
