package org.sopt.poti.domain.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "분철글 ID를 입력해주세요.") Long groupBuyPostId,
        @NotNull(message = "배송 방법을 선택해주세요.") Long shippingId,
        @NotNull(message = "배송지 정보를 입력해주세요.") @Valid DeliveryInfoRequest deliveryInfo,
        @NotEmpty(message = "주문 항목을 선택해주세요.") List<OrderItemRequest> items,
        boolean saveAsMyAddress
) {}
