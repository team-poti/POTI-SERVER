package org.sopt.poti.domain.delivery.dto.response;

public record DeliveryOptionsResponse(
    Long deliveryId,
    String name,
    Integer price
) {

}
