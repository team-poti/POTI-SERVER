package org.sopt.poti.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(length = 20)
    private String phone;

    public DeliveryInfo(
            String receiverName,
            String zipcode,
            String addressLine,
            String phone
    ) {
        this.receiverName = receiverName;
        this.zipcode = zipcode;
        this.addressLine = addressLine;
        this.phone = phone;
    }
}
