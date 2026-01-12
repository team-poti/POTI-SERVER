package org.sopt.poti.domain.groupbuy.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.entity.BaseTimeEntity;

import jakarta.persistence.Index; // Import Index

@Getter
@Entity
@Table(name = "group_buy_posts", indexes = { // 인덱스 추가
    @Index(name = "idx_group_buy_post_title", columnList = "title")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyPost extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "recruit_deadline", nullable = false) // 마감일 필수
  private LocalDate recruitDeadline;

  @Column(name = "bank_name", length = 50, nullable = false) // 은행명 필수
  private String bankName;

  @Column(name = "account_number", length = 50, nullable = false) // 계좌번호 필수
  private String accountNumber;

  @Column(name = "account_holder", length = 50)
  private String accountHolder;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private GroupBuyPostStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User leader; // 총대

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artist_id", nullable = false)
  private Artist artist;

  @OneToMany(mappedBy = "groupBuyPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupBuyOption> options = new ArrayList<>();

  @OneToMany(mappedBy = "groupBuyPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "groupBuyPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupBuyShipping> shippings = new ArrayList<>();

  @Builder
  private GroupBuyPost(
      String title,
      String content,
      LocalDate recruitDeadline,
      String bankName,
      String accountNumber,
      User leader,
      Artist artist
  ) {
    this.title = title;
    this.content = content;
    this.recruitDeadline = recruitDeadline;
    this.bankName = bankName;
    this.accountNumber = accountNumber;
    // this.accountHolder = accountHolder;
    this.leader = leader;
    this.artist = artist;
    this.status = GroupBuyPostStatus.RECRUITING; // 초기 상태: 모집 중
  }

  public static GroupBuyPost create(
      String title,
      String content,
      LocalDate recruitDeadline,
      String bankName,
      String accountNumber,
      User leader,
      Artist artist
  ) {
    return GroupBuyPost.builder()
        .title(title)
        .content(content)
        .recruitDeadline(recruitDeadline)
        .bankName(bankName)
        .accountNumber(accountNumber)
        // .accountHolder(accountHolder)
        .leader(leader)
        .artist(artist)
        .build();
  }

  // 연관관계 편의 메서드
  public void addOption(GroupBuyOption option) {
    this.options.add(option);
    option.setGroupBuyPost(this); // 양방향 연관관계 설정
  }

  public void addImage(ItemImage image) {
    this.images.add(image);
    image.setGroupBuyPost(this); // 양방향 연관관계 설정
  }

  public void addShipping(GroupBuyShipping shipping) {
    this.shippings.add(shipping);
    shipping.setGroupBuyPost(this); // 양방향 연관관계 설정
  }
}