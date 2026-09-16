package org.sopt.poti.domain.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.dto.request.StartDeliveryRequest;
import org.sopt.poti.domain.delivery.dto.response.DeliveryOptionsResponse;
import org.sopt.poti.domain.delivery.dto.response.StartDeliveryResponse;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Delivery", description = "배송 관련 API")
public class DeliveryController {

  private final DeliveryService deliveryService;
  private final OrderService orderService;

  @GetMapping("/shippings")
  @Operation(summary = "배송 옵션 조회", description = "게시글 작성 시 선택 가능한 배송 방법 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<DeliveryOptionsResponse>>> getDeliveryOptions() {
    List<DeliveryOptionsResponse> response = deliveryService.getDeliveryOptions();
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
  }

  @PatchMapping("/orders/{orderId}/deliveries")
  @Operation(summary = "주문에 해당하는 운송장 번호 등록", description = "주문에 해당하는 운송장 번호와 운송사를 등록할 수 있습니다.")
  public ResponseEntity<ApiResponse<StartDeliveryResponse>> startDelivery(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable Long orderId,
      @RequestBody @Valid StartDeliveryRequest startDeliveryRequest
  ) {
    StartDeliveryResponse startDeliveryResponse = orderService.startOrderDelivery(
        userPrincipal.getUserId(), orderId, startDeliveryRequest);

    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, startDeliveryResponse));
  }
}
