package org.sopt.poti.domain.review.repository;

import org.sopt.poti.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsByOrder_Id(Long orderId);

  long countBySeller_Id(Long sellerId);
}