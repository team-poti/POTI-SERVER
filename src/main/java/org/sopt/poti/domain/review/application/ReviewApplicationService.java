package org.sopt.poti.domain.review.application;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.service.GroupBuyService;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.review.dto.request.ReviewRequest;
import org.sopt.poti.domain.review.service.ReviewService;
import org.sopt.poti.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewApplicationService {

  private final OrderService orderService;
  private final ReviewService reviewService;
  private final UserService userService;
  private final GroupBuyService groupBuyService;

  @Transactional
  public Long createReview(Long writerUserId, ReviewRequest request) {
    Long orderId = request.transactionId();
    int score = request.star();

    Order order = orderService.getOrderById(orderId);

    orderService.validateDelivered(order);
    orderService.validateOrderOwner(order, writerUserId);

    Long reviewId = reviewService.createReviewEntity(writerUserId, order, score);

    Long sellerId = order.getGroupBuyPost().getLeader().getId();
    Long postId = order.getGroupBuyPost().getId();

    // 1 팟 내부 단순평균 업데이트
    groupBuyService.addPostRating(postId, score);

    // 2 최신 팟 상태 조회 및 데이터 수집
    GroupBuyPost post = groupBuyService.getPostWithLock(postId);
    Integer reviewCount = post.getRatingCount();
    Double postAvg = post.getRatingAvg();

    int postCount = groupBuyService.countPostsByLeader(sellerId);

    // 3 판매자 평점 반영
    userService.applyPostContribution(sellerId, postId, postAvg, reviewCount, postCount);

    return reviewId;
  }
}
