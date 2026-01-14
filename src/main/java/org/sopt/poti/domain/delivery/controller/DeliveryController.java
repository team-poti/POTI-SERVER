package org.sopt.poti.domain.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.dto.response.DeliveryOptionsResponse;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
@Tag(name = "Delivery", description = "배송 관련 API")
public class DeliveryController {

  private final DeliveryService deliveryService;

  @GetMapping
  @Operation(summary = "배송 옵션 조회", description = "게시글 작성 시 선택 가능한 배송 방법 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<DeliveryOptionsResponse>>> getDeliveryOptions() {
    List<DeliveryOptionsResponse> response = deliveryService.getDeliveryOptions();
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
  }
}
