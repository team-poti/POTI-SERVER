package org.sopt.poti.domain.user.dto.request;

public record UpdateAddressRequest(
    String receiverName,
    String zipcode,
    String address,
    String addressDetail,
    String phone
) {}
