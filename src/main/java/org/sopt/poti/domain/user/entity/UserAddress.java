package org.sopt.poti.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "receiver_name", length = 50)
  private String receiverName;

  @Column(length = 10)
  private String zipcode;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "address_detail", length = 255)
  private String addressDetail;

  @Column(length = 20)
  private String phone;

  public static UserAddress create(User user, String receiverName, String zipcode,
      String address, String addressDetail, String phone) {
    UserAddress userAddress = new UserAddress();
    userAddress.user = user;
    userAddress.receiverName = receiverName;
    userAddress.zipcode = zipcode;
    userAddress.address = address;
    userAddress.addressDetail = addressDetail;
    userAddress.phone = phone;
    return userAddress;
  }

  public void update(String receiverName, String zipcode, String address, String addressDetail, String phone) {
    this.receiverName = receiverName;
    this.zipcode = zipcode;
    this.address = address;
    this.addressDetail = addressDetail;
    this.phone = phone;
  }
}
