package org.sopt.poti.domain.user.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(
    String receiverName,
    String zipcode,
    @Size(max = 255) String address,
    @Size(max = 255) String addressDetail,
    String phone
) {}
