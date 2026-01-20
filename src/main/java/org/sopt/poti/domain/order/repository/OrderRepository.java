package org.sopt.poti.domain.order.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

  int countByUser_Id(Long userId);

  int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses);

  boolean existsByOrderNumber(String orderNumber);

  @EntityGraph(attributePaths = {
      "groupBuyPost",
      "groupBuyPost.artist"
  })
  List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

  void deleteByUser_Id(Long userId);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.groupBuyPost.id = :postId AND o.status <> :status")
  long countUnpaidOrders(@Param("postId") Long postId, @Param("status") OrderStatus status);

  @Query("SELECT o FROM Order o JOIN FETCH o.groupBuyPost p JOIN FETCH p.leader WHERE o.id = :orderId")
  Optional<Order> findByIdWithPostAndLeader(@Param("orderId") Long orderId);

  @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN FETCH o.user u
        JOIN FETCH o.groupBuyPost p
        JOIN FETCH p.artist a
        JOIN FETCH o.deliveryMethod dm
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.groupBuyOption gbo
        LEFT JOIN FETCH gbo.member m
        WHERE o.id = :orderId
      """)
  Optional<Order> findWithDetailsById(@Param("orderId") Long orderId);

  // 특정 게시글에서, 주어진 상태 목록에 해당하는 주문이 몇 개인지 조회
  long countByGroupBuyPostIdAndStatusIn(Long postId, Collection<OrderStatus> statuses);
}