package org.sopt.poti.domain.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StartDeliveryRequest(
    @Schema(description = "배송사", example = "우체국택배")
    @NotBlank(message = "배송사는 필수입니다.")
    String carrier,
    @Schema(description = "운송장 번호", example = "1234-5678-9012")
    @NotBlank(message = "운송장 번호는 필수입니다.")
    String trackingNumber
) {
  
}
