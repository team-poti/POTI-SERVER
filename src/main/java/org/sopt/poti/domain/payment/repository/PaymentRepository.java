package org.sopt.poti.domain.payment.repository;

import org.sopt.poti.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdOrderByIdDesc(Long orderId);
}