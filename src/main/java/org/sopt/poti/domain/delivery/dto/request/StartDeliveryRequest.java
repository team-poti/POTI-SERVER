package org.sopt.poti.domain.delivery.dto.request;

public record StartDeliveryRequest(
    String carrier,
    String trackingNumber
) {
  
}
