package org.sopt.poti.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.delivery.repository.DeliveryMethodRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryMethodRepository deliveryMethodRepository;

    public DeliveryMethod getDeliveryMethodById(Long deliveryMethodId) {
        return deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.DELIVERY_METHOD_NOT_FOUND));
    }
}
