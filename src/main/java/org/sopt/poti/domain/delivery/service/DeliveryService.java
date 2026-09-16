package org.sopt.poti.domain.delivery.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.dto.response.DeliveryOptionsResponse;
import org.sopt.poti.domain.delivery.entity.Delivery;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.delivery.repository.DeliveryMethodRepository;
import org.sopt.poti.domain.delivery.repository.DeliveryRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

  private final DeliveryMethodRepository deliveryMethodRepository;
  private final DeliveryRepository deliveryRepository;

  public DeliveryMethod getDeliveryMethodById(Long deliveryMethodId) {
    return deliveryMethodRepository.findById(deliveryMethodId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.DELIVERY_METHOD_NOT_FOUND));
  }

  public List<DeliveryOptionsResponse> getDeliveryOptions() {
    return deliveryMethodRepository.findAll().stream()
        .map(deliveryMethod -> new DeliveryOptionsResponse(
            deliveryMethod.getId(),
            deliveryMethod.getName(),
            deliveryMethod.getPrice()
        ))
        .toList();
  }

  @Transactional
  public void saveDelivery(Delivery delivery) {
    deliveryRepository.save(delivery);
  }

  public boolean existsDeliveryByOrderId(Long orderId) {
    return deliveryRepository.existsDeliveryByOrderId(orderId);
  }

  public Optional<Delivery> findTopByOrder_IdOrderByIdDesc(Long orderId) {
    return deliveryRepository.findTopByOrder_IdOrderByIdDesc(orderId);
  }
}
