package org.sopt.poti.domain.delivery.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "")
public class DeliveryController {

  private final DeliveryService deliveryService;

  @GetMapping("/shippings")
  public ResponseEntity<ApiResponse<?>> getShippings() {
    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, deliveryService.getDeliveryOptions()));
  }
}
