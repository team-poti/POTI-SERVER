package org.sopt.poti.domain.user.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.sopt.poti.domain.user.entity.SellerPostRatingContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SellerPostRatingContributionRepository
    extends JpaRepository<SellerPostRatingContribution, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from SellerPostRatingContribution c where c.sellerId = :sellerId and c.postId = :postId")
  Optional<SellerPostRatingContribution> findBySellerIdAndPostIdWithLock(
      @Param("sellerId") Long sellerId,
      @Param("postId") Long postId
  );
}