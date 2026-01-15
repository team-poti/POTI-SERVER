package org.sopt.poti.domain.groupbuy.repository;


import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBuyOptionRepository extends JpaRepository<GroupBuyOption, Long> {

    List<GroupBuyOption> findAllByIdIn(List<Long> ids);
}