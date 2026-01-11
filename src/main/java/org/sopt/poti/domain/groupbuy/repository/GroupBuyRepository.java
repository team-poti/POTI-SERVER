package org.sopt.poti.domain.groupbuy.repository;

import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuyPost, Long>, GroupBuyRepositoryCustom {
}
