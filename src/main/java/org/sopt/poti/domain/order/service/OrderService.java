package org.sopt.poti.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public int countByUser_Id(Long userId) {
        return orderRepository.countByUser_Id(userId);
    }

    public int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses) {
        return orderRepository.countByUser_IdAndStatusIn(userId, statuses);
    }
}