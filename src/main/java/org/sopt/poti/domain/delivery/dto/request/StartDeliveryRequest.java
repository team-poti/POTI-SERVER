package org.sopt.poti.domain.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record StartDeliveryRequest(
    @Schema(description = "배송사", example = "우체국택배")
    String carrier,
    @Schema(description = "운송장 번호", example = "1234-5678-9012")
    String trackingNumber
) {
  
}
