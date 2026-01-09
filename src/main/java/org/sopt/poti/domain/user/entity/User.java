package org.sopt.poti.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, unique = true)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "rating_avg")
    private Double ratingAvg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_artist_id", nullable = false)
    private Artist favoriteArtist;

    public static User create(String email, String nickname, String profileImageUrl, Artist favoriteArtist) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        user.favoriteArtist = favoriteArtist;
        user.ratingAvg = 0.0;
        user.lastActiveAt = LocalDateTime.now();
        return user;
    }
}