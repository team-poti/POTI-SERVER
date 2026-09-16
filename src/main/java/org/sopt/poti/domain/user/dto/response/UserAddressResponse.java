package org.sopt.poti.domain.user.dto.response;

import org.sopt.poti.domain.user.entity.UserAddress;

public record UserAddressResponse(
    String receiverName,
    String zipcode,
    String address,
    String addressDetail,
    String phone
) {
  public static UserAddressResponse from(UserAddress userAddress) {
    return new UserAddressResponse(
        userAddress.getReceiverName(),
        userAddress.getZipcode(),
        userAddress.getAddress(),
        userAddress.getAddressDetail(),
        userAddress.getPhone()
    );
  }
}
