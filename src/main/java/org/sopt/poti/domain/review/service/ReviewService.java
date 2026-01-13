package org.sopt.poti.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.review.dto.request.ReviewRequest;
import org.sopt.poti.domain.review.entity.Review;
import org.sopt.poti.domain.review.repository.ReviewRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Long createReview(Long writerUserId, ReviewRequest request) {
        Long orderId = request.transactionId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorStatus.ORDER_NOT_COMPLETED);
        }

        if (!order.getUser().getId().equals(writerUserId)) {
            throw new BusinessException(ErrorStatus.REVIEW_FORBIDDEN);
        }

        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new BusinessException(ErrorStatus.REVIEW_ALREADY_EXISTS);
        }

        User writer = userRepository.findById(writerUserId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

        User seller = order.getGroupBuyPost().getLeader();

        Review review = Review.create(request.star(), order, writer, seller);

        Review saved = reviewRepository.save(review);

        double rawAvg = reviewRepository.avgScoreBySellerId(seller.getId());
        double roundAvg = Math.round(rawAvg * 10) / 10.0;

        seller.updateRatingAvg(roundAvg);

        return saved.getId();
    }

}
