package org.sopt.poti.domain.delivery.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.poti.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  List<Delivery> findByOrder_Id(Long orderId);

  boolean existsDeliveryByOrderId(Long orderId);

  Optional<Delivery> findTopByOrder_IdOrderByIdDesc(Long orderId);

  void deleteByOrder_Id(Long orderId);
}
