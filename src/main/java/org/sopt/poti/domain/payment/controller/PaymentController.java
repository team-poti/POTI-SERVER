package org.sopt.poti.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.payment.dto.request.DepositFormRequest;
import org.sopt.poti.domain.payment.dto.response.DepositFormResponse;
import org.sopt.poti.domain.payment.service.PaymentService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DepositFormResponse>> submitDepositForm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid DepositFormRequest request
    ) {
        Long userId = userPrincipal.getUserId();
        DepositFormResponse data = paymentService.submitDepositForm(userId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, data));
    }

}
