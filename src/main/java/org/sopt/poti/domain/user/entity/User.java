package org.sopt.poti.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.global.entity.BaseSoftDeleteEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseSoftDeleteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 255)
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

  @Column(name = "rating_sum", nullable = false)
  private long ratingSum = 0L;

  @Column(name = "rating_count", nullable = false)
  private int ratingCount = 0;

  @Column(name = "rating_weighted_sum", nullable = false)
  private double ratingWeightedSum = 0.0;

  @Column(name = "rating_weight_total", nullable = false)
  private double ratingWeightTotal = 0.0;

  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "favorite_artist_id", nullable = true)
  private Artist favoriteArtist;

  @Builder
  private User(String socialId, SocialType socialType, String email, String nickname,
      String profileImageUrl, Artist favoriteArtist, Role role) {
    this.socialId = socialId;
    this.socialType = socialType;
    this.email = email;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.favoriteArtist = favoriteArtist;
    this.role = role;
    this.ratingAvg = 0.0;
    this.status = UserStatus.ACTIVE;
    this.lastActiveAt = LocalDateTime.now();
  }

  public static User createSocialUser(String socialId, SocialType socialType, String email,
      String nickname, String profileImageUrl, Artist favoriteArtist) {
    return User.builder()
        .socialId(socialId)
        .socialType(socialType)
        .email(email)
        .nickname(nickname)
        .profileImageUrl(profileImageUrl)
        .favoriteArtist(favoriteArtist)
        .role(Role.USER)
        .build();
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = LocalDateTime.now();
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updateFavoriteArtist(Artist artist) {
    this.favoriteArtist = artist;
  }

  public void updateRatingAvg(double avg) {
    this.ratingAvg = avg;
  }

  public void withdraw() {
    this.status = UserStatus.WITHDRAWN;
    this.deletedAt = LocalDateTime.now();
    this.nickname = "탈퇴한 사용자";
    this.email = null;
    this.profileImageUrl = null;
    this.socialId = "deleted_" + this.id + "_" + UUID.randomUUID(); // 유니크 제약 회피
  }
}