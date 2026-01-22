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
import jakarta.persistence.Index;
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
import org.sopt.poti.domain.item.entity.Item;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.entity.BaseTimeEntity;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;

@Getter
@Entity
@Table(name = "group_buy_posts", indexes = { // 인덱스 추가
    @Index(name = "idx_group_buy_post_title", columnList = "title"),
    @Index(name = "idx_group_buy_post_order_number", columnList = "order_number")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyPost extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_number", length = 20, unique = true)
  private String orderNumber;

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

  @Column(name = "representative_image_url") // 대표 이미지 URL (성능 최적화용)
  private String representativeImageUrl;

  @Column(name = "goal_quantity", nullable = false)
  private int goalQuantity;  // 최대 모집 인원

  @Column(name = "current_quantity", nullable = false)
  private int currentQuantity;  // 현재 인원

  @Column(name = "rating_avg", nullable = false)
  private double ratingAvg = 0.0; //공구 글 당 평점

  @Column(name = "rating_sum", nullable = false)
  private long ratingSum = 0L;

  @Column(name = "rating_count", nullable = false)
  private int ratingCount = 0;

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

  @OneToMany(mappedBy = "groupBuyPost")
  private List<Order> orders = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @Builder
  private GroupBuyPost(
      String orderNumber,
      String title,
      String content,
      LocalDate recruitDeadline,
      String bankName,
      String accountNumber,
      int goalQuantity,
      String representativeImageUrl,
      User leader,
      Artist artist
  ) {
    this.orderNumber = orderNumber;
    this.title = title;
    this.content = content;
    this.recruitDeadline = recruitDeadline;
    this.bankName = bankName;
    this.accountNumber = accountNumber;
    this.goalQuantity = goalQuantity;
    this.currentQuantity = 0;
    this.representativeImageUrl = representativeImageUrl;
    this.leader = leader;
    this.artist = artist;
    this.status = GroupBuyPostStatus.RECRUITING;
  }

  public static GroupBuyPost create(
      String orderNumber,
      String title,
      String content,
      LocalDate recruitDeadline,
      String bankName,
      String accountNumber,
      int goalQuantity,
      String representativeImageUrl,
      User leader,
      Artist artist
  ) {
    return GroupBuyPost.builder()
        .orderNumber(orderNumber)
        .title(title)
        .content(content)
        .recruitDeadline(recruitDeadline)
        .bankName(bankName)
        .accountNumber(accountNumber)
        .goalQuantity(goalQuantity)
        .representativeImageUrl(representativeImageUrl)
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

  public void addOrder(Order order) {
    this.orders.add(order);
    order.setGroupBuyPost(this);
  }

  // 분철글 상태 변경 메서드
  public void updateStatus(GroupBuyPostStatus status) {
    this.status = status;
  }

  //분철글 참여자 다 찼을때 분철글 상태 변경하는 메서드
  public void increaseCurrentQuantity(int count) {
    if (count <= 0) {
      throw new BusinessException(ErrorStatus.GROUP_BUY_POST_INVALID_INCREASE_COUNT);
    }
    this.currentQuantity += count;

    if (this.currentQuantity >= this.goalQuantity) {
      this.currentQuantity = this.goalQuantity;
      this.status = GroupBuyPostStatus.CLOSED;
    }
  }


  // 분철글 상태 변경 메서드 (참여자 모두의 OrderStatus가 배송완료일때, GroupBuyPostStatus도 배송완료로 변경)
  public void completePostDelivery() {
    this.status = GroupBuyPostStatus.DELIVERED;
  }

  public void addRating(int score) {
    validateScore(score);

    this.ratingSum += score;
    this.ratingCount += 1;

    double avg = (double) this.ratingSum / this.ratingCount;
    this.ratingAvg = Math.round(avg * 10) / 10.0;
  }

  private void validateScore(int score) {
    if (score < 1 || score > 5) {
      throw new BusinessException(ErrorStatus.INVALID_RATING_SCORE);
    }
  }

}