package org.sopt.poti.domain.order.repository;

import org.sopt.poti.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  OrderRepository extends JpaRepository<Order,Long> {
}
