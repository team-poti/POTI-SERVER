package org.sopt.poti.domain.order.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.dto.request.CreateOrderRequest;
import org.sopt.poti.domain.order.dto.response.CreateOrderResponse;
import org.sopt.poti.domain.order.service.OrderCreateService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders", description = "공구 주문 관련 API")
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderCreateService orderCreateService;

  @Operation(summary = "공구 참여(주문) 폼 제출", description = "유저가 공구 참여(구매)를 위한 폼을 작성합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody @Valid CreateOrderRequest request
  ) {
    CreateOrderResponse data = orderCreateService.createOrder(userPrincipal.getUserId(), request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(SuccessStatus.CREATED, data));
  }
}