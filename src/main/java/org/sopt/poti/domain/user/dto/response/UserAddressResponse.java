package org.sopt.poti.domain.user.dto.response;

import org.sopt.poti.domain.user.entity.UserAddress;

public record UserAddressResponse(
    String receiverName,
    String zipcode,
    String addressLine,
    String phone
) {
  public static UserAddressResponse from(UserAddress address) {
    return new UserAddressResponse(
        address.getReceiverName(),
        address.getZipcode(),
        address.getAddressLine(),
        address.getPhone()
    );
  }
}
