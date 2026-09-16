package org.sopt.poti.domain.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.payment.dto.request.DepositFormRequest;
import org.sopt.poti.domain.payment.dto.response.DepositFormResponse;
import org.sopt.poti.domain.payment.dto.response.OrderConfirmResponse;
import org.sopt.poti.domain.payment.service.PaymentService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payments", description = "결제 관련 API")
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "구매자용 입금 폼 제출", description = "구매자가 입금자명과 입금 시간을 입력해 전송합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<DepositFormResponse>> submitDepositForm(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestBody @Valid DepositFormRequest request
  ) {
    Long userId = userPrincipal.getUserId();
    DepositFormResponse data = paymentService.submitDepositForm(userId, request);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
  }

  @PatchMapping("/{orderId}/confirm")
  @Operation(summary = "모집자(총대)가 참여자의 입금 상태를 입금 완료로 변경", description =
      "모집자가 참여자의 입금 상태를 입금 완료로 변경합니다."
          + "\n 상태 변경시 해당 분철글에 대한 모든 주문들의 상태를 조회해 전부 입금 완료 상태(PAID)이면 분철글의 상태도 입금 완료 상태로 변경합니다.")
  public ResponseEntity<ApiResponse<OrderConfirmResponse>> confirmPayment(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable(name = "orderId") Long orderId
  ) {
    OrderConfirmResponse orderConfirmResponse = paymentService.confirmPayment(
        userPrincipal.getUserId(), orderId);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, orderConfirmResponse));
  }

}
