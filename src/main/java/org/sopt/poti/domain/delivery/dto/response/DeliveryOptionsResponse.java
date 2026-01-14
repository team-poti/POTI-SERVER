package org.sopt.poti.domain.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeliveryOptionsResponse(
    @Schema(description = "배송 방법 ID", example = "1")
    Long deliveryId,

    @Schema(description = "배송 방법명", example = "일반 택배")
    String name,

    @Schema(description = "기본 배송비", example = "3000")
    Integer price
) {

}
