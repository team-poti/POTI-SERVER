package org.sopt.poti.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.review.entity.Review;
import org.sopt.poti.domain.review.repository.ReviewRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final UserService userService;

  public Long createReviewEntity(Long writerUserId, Order order, int score) {
    Long orderId = order.getId();

    if (reviewRepository.existsByOrder_Id(orderId)) {
      throw new BusinessException(ErrorStatus.REVIEW_ALREADY_EXISTS);
    }

    User writer = userService.getUserById(writerUserId);
    User seller = order.getGroupBuyPost().getLeader();

    Review review = Review.create(score, order, writer, seller);

    try {
      Review saved = reviewRepository.save(review);
      return saved.getId();
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorStatus.REVIEW_ALREADY_EXISTS);
    }
  }

  public Integer countReviewsForSeller(Long sellerId) {
    return (int) reviewRepository.countBySeller_Id(sellerId);
  }
}
