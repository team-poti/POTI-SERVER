package org.sopt.poti.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "delivery_methods")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(length = 50)
    private String carrier;

    public static DeliveryMethod create(String name, int price, String carrier) {
        DeliveryMethod dm = new DeliveryMethod();
        dm.name = name;
        dm.price = price;
        dm.carrier = carrier;
        return dm;
    }
}