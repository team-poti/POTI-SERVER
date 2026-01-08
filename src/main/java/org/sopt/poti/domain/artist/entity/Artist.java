package org.sopt.poti.domain.artist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "artists")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "logo_image_url", length = 255)
    private String logoImageUrl;

    public static Artist create(String name, String logoImageUrl) {
        Artist artist = new Artist();
        artist.name = name;
        artist.logoImageUrl = logoImageUrl;
        return artist;
    }
}