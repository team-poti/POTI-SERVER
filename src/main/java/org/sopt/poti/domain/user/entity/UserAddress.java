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

  @Column(name = "address_line", length = 255)
  private String addressLine;

  @Column(length = 20)
  private String phone;

  public static UserAddress create(User user, String receiverName, String zipcode,
      String addressLine, String phone) {
    UserAddress address = new UserAddress();
    address.user = user;
    address.receiverName = receiverName;
    address.zipcode = zipcode;
    address.addressLine = addressLine;
    address.phone = phone;
    return address;
  }

  public void update(String receiverName, String zipcode, String addressLine, String phone) {
    this.receiverName = receiverName;
    this.zipcode = zipcode;
    this.addressLine = addressLine;
    this.phone = phone;
  }
}
