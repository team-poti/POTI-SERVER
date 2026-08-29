package org.sopt.poti.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryInfo {

  @Column(name = "receiver_name", length = 50)
  private String receiverName;

  @Column(length = 10)
  private String zipcode;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "address_detail", length = 255)
  private String addressDetail;

  @Column(length = 20)
  @NotBlank(message = "휴대폰 번호는 필수입니다.")
  //@Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
  private String phone;

  public DeliveryInfo(
      String receiverName,
      String zipcode,
      String address,
      String addressDetail,
      String phone
  ) {
    this.receiverName = receiverName;
    this.zipcode = zipcode;
    this.address = address;
    this.addressDetail = addressDetail;
    this.phone = phone;
  }
}
