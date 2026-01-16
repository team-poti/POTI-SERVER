package org.sopt.poti.domain.payment.repository;

import org.sopt.poti.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  boolean existsByOrder_Id(Long orderId);
}