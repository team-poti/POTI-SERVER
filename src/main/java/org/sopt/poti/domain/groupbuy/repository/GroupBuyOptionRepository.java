package org.sopt.poti.domain.groupbuy.repository;


import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyOptionRepository extends JpaRepository<GroupBuyOption, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT o FROM GroupBuyOption o WHERE o.id IN :ids ORDER BY o.id")
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
  List<GroupBuyOption> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}