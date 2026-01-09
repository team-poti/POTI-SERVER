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

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(length = 20)
    private String phone;

    public DeliveryInfo(
            String receiverName,
            String zipcode,
            String addressLine1,
            String addressLine2,
            String phone
    ) {
        this.receiverName = receiverName;
        this.zipcode = zipcode;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.phone = phone;
    }
}
