package org.sopt.poti.domain.artist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(name = "member_image_url", length = 255)
    private String memberImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public static Member create(String name, String memberImageUrl, Artist artist) {
        Member member = new Member();
        member.name = name;
        member.memberImageUrl = memberImageUrl;
        member.artist = artist;
        return member;
    }
}