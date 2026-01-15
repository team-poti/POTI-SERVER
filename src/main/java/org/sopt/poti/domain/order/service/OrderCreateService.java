package org.sopt.poti.domain.order.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyShipping;
import org.sopt.poti.domain.groupbuy.service.GroupBuyService;
import org.sopt.poti.domain.order.dto.request.CreateOrderRequest;
import org.sopt.poti.domain.order.dto.request.OrderItemRequest;
import org.sopt.poti.domain.order.dto.response.CreateOrderResponse;
import org.sopt.poti.domain.order.entity.DeliveryInfo;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCreateService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  private final GroupBuyService groupBuyService;
  private final UserService userService;

  @Transactional
  public CreateOrderResponse createOrder(Long userId, CreateOrderRequest request) {

    // 1 게시글 조회
    User user = userService.getUserById(userId);

    GroupBuyPost post = groupBuyService.getRecruitingPostById(request.groupBuyPostId());

    // 2 공구 배송 옵션 조회
    GroupBuyShipping shipping = groupBuyService.getShippingInPost(request.shippingId(),
        post.getId());
    int shippingPrice = shipping.getPrice();
    DeliveryMethod deliveryMethod = shipping.getDeliveryMethod();

    // 3 옵션(ex 장원영/ 안유진) id 조회
    List<Long> optionIds = request.items().stream()
        .map(OrderItemRequest::groupBuyOptionId)
        .toList();

    // 4 중복 옵션 검증
    Set<Long> uniqueIds = new HashSet<>(optionIds);
    if (uniqueIds.size() != optionIds.size()) {
      throw new BusinessException(ErrorStatus.DUPLICATE_ORDER_OPTION);
    }

    // 5 옵션 조회 및 검증
    List<GroupBuyOption> options = groupBuyService.getOptionsInPost(optionIds, post.getId());

    // 6 배송정보
    DeliveryInfo deliveryInfo = new DeliveryInfo(
        request.deliveryInfo().receiverName(),
        request.deliveryInfo().zipcode(),
        request.deliveryInfo().addressLine(),
        request.deliveryInfo().phone()
    );

    // 7 총 금액 계산 = 옵션들 합 + 공구 배송비
    Map<Long, GroupBuyOption> optionMap = options.stream()
        .collect(Collectors.toMap(GroupBuyOption::getId, o -> o));

    int itemsAmount = 0;
    for (OrderItemRequest itemReq : request.items()) {
      if (itemReq.count() <= 0) {
        throw new BusinessException(ErrorStatus.ORDER_ITEM_INVALID_COUNT);
      }
      GroupBuyOption option = optionMap.get(itemReq.groupBuyOptionId());
      itemsAmount += option.getPrice() * itemReq.count();
    }

    int totalAmount = itemsAmount + shippingPrice;

    // 8 Order 저장
    Order order = Order.create(
        post,
        user,
        deliveryMethod,
        totalAmount,
        deliveryInfo
    );

    Order savedOrder = orderRepository.save(order);

    // 9 OrderItem 저장
    List<OrderItem> orderItems = request.items().stream()
        .map(itemReq -> {
          GroupBuyOption option = optionMap.get(itemReq.groupBuyOptionId());
          return OrderItem.create(itemReq.count(), option.getPrice(), savedOrder, option);
        })
        .toList();

    orderItemRepository.saveAll(orderItems);

    return new CreateOrderResponse(savedOrder.getId());
  }
}