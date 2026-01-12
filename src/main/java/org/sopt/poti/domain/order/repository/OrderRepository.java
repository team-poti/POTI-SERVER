package org.sopt.poti.domain.order.repository;

import java.util.List;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    int countByUser_Id(Long userId);

    int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses);
}