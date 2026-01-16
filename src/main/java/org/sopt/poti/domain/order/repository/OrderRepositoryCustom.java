package org.sopt.poti.domain.order.repository;

import java.util.List;
import org.sopt.poti.domain.order.entity.Order;

public interface OrderRepositoryCustom {

  List<Order> findAllOrdersWithBasicInfo(Long postId);
}
