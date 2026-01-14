package org.sopt.poti.domain.order.repository;

import java.util.List;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  @Query("SELECT oi FROM OrderItem oi " +
      "JOIN FETCH oi.order o " +
      "JOIN FETCH o.user u " +
      "JOIN FETCH oi.groupBuyOption gbo " +
      "JOIN FETCH gbo.member m " +
      "WHERE gbo.id IN :optionIds")
  List<OrderItem> findAllByGroupBuyOptionIdIn(@Param("optionIds") List<Long> optionIds);

  void deleteByGroupBuyOptionIdIn(List<Long> optionIds);
}
