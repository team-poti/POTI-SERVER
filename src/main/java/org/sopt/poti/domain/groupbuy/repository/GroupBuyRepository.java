package org.sopt.poti.domain.groupbuy.repository;

import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuyPost, Long>, GroupBuyRepositoryCustom {
    int countByLeader_Id(Long userId);

    int countByLeader_IdAndStatusIn(Long userId, List<GroupBuyPostStatus> statuses);

}
