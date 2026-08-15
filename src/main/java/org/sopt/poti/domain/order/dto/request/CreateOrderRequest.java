package org.sopt.poti.domain.order.dto.request;

import java.util.List;

public record CreateOrderRequest (
        Long groupBuyPostId,
        Long shippingId,
        DeliveryInfoRequest deliveryInfo,
        List<OrderItemRequest> items,
        boolean saveAsMyAddress
){}
