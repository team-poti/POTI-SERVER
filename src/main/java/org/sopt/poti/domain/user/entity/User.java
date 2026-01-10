package org.sopt.poti.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder 사용을 위한 추가
@Builder // 빌더 패턴 사용을 위한 추가
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255) // unique = true 제거 (socialId로 유니크 보장)
    private String email;

    @Column(name = "social_id", length = 255, unique = true, nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

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

    // 소셜 로그인으로 사용자 생성 시 사용하는 빌더 메서드
    public static User createSocialUser(String socialId, SocialType socialType, String email, String nickname, String profileImageUrl, Artist favoriteArtist) {
        return User.builder()
                .socialId(socialId)
                .socialType(socialType)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .favoriteArtist(favoriteArtist)
                .role(Role.USER) // 기본값으로 USER 설정
                .ratingAvg(0.0)
                .lastActiveAt(LocalDateTime.now())
                .build();
    }
}