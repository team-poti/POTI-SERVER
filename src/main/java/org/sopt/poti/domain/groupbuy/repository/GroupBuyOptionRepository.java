package org.sopt.poti.domain.groupbuy.repository;


import java.util.List;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyOptionRepository extends JpaRepository<GroupBuyOption, Long> {

  @Query("SELECT o FROM GroupBuyOption o WHERE o.id IN :ids ORDER BY o.id")
  List<GroupBuyOption> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}