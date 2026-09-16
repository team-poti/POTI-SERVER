package org.sopt.poti.domain.review.repository;

import org.sopt.poti.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsByOrder_Id(Long orderId);

  @Query("select coalesce(avg(r.score), 0) from Review r where r.seller.id = :sellerId")
  double avgScoreBySellerId(@Param("sellerId") Long sellerId);

  long countBySeller_Id(Long sellerId);
}