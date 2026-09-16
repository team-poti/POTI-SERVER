package org.sopt.poti.domain.groupbuy.repository;

import java.util.Optional;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyShipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyShippingRepository extends JpaRepository<GroupBuyShipping, Long> {

    Optional<GroupBuyShipping> findByIdAndGroupBuyPost_Id(Long shippingId, Long postId);
}