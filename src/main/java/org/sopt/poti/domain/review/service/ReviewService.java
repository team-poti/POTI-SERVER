package org.sopt.poti.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.global.external.mixpanel.MixpanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.review.dto.request.ReviewRequest;
import org.sopt.poti.domain.review.entity.Review;
import org.sopt.poti.domain.review.repository.ReviewRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final OrderService orderService;
  private final UserService userService;

  @Autowired(required = false)
  private MixpanelService mixpanelService;

  public Long createReview(Long writerUserId, ReviewRequest request) {
    Long orderId = request.transactionId();

    Order order = orderService.getOrderById(orderId);

    orderService.validateDelivered(order);
    orderService.validateOrderOwner(order, writerUserId);

    if (reviewRepository.existsByOrder_Id(orderId)) {
      throw new BusinessException(ErrorStatus.REVIEW_ALREADY_EXISTS);
    }

    User writer = userService.getUserById(writerUserId);

    User seller = order.getGroupBuyPost().getLeader();

    Review review = Review.create(request.star(), order, writer, seller);
    Review saved = reviewRepository.save(review);

    double rawAvg = reviewRepository.avgScoreBySellerId(seller.getId());
    double roundAvg = Math.round(rawAvg * 10) / 10.0;
    seller.updateRatingAvg(roundAvg);

    if (mixpanelService != null) {
      mixpanelService.track(writerUserId, "Review Submitted", java.util.Map.of(
          "split_id", String.valueOf(order.getGroupBuyPost().getId()),
          "transaction_id", String.valueOf(order.getId()),
          "rating", request.star()
      ));
    }

    return saved.getId();
  }

  public Integer countReviewsForSeller(Long sellerId) {
    return (int) reviewRepository.countBySeller_Id(sellerId);
  }
}
