package org.sopt.poti.domain.order.repository;

import java.util.List;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

  int countByUser_Id(Long userId);

  int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses);

  @EntityGraph(attributePaths = {
      "groupBuyPost",
      "groupBuyPost.artist"
  })
  List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

  void deleteByUser_Id(Long userId);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.groupBuyPost.id = :postId AND o.status <> :status")
  long countUnpaidOrders(@Param("postId") Long postId, @Param("status") OrderStatus status);
}