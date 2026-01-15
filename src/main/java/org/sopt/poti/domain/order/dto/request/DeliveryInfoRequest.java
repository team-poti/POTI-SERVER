package org.sopt.poti.domain.order.dto.request;

public record DeliveryInfoRequest (
        String receiverName,
        String zipcode,
        String addressLine,
        String phone
){ }
