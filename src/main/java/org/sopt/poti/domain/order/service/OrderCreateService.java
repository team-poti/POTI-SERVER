package org.sopt.poti.domain.order.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyShipping;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyOptionRepository;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyShippingRepository;
import org.sopt.poti.domain.order.dto.request.CreateOrderRequest;
import org.sopt.poti.domain.order.dto.request.OrderItemRequest;
import org.sopt.poti.domain.order.dto.response.CreateOrderResponse;
import org.sopt.poti.domain.order.entity.DeliveryInfo;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCreateService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final GroupBuyRepository groupBuyRepository;
    private final GroupBuyOptionRepository groupBuyOptionRepository;
    private final GroupBuyShippingRepository groupBuyShippingRepository;

    private final UserRepository userRepository;

    public CreateOrderResponse createOrder(Long userId, CreateOrderRequest request) {

        // 1 게시글 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

        GroupBuyPost post = groupBuyRepository.findById(request.groupBuyPostId())
                .orElseThrow(() -> new BusinessException(ErrorStatus.POST_NOT_FOUND));

        // 2 공구 배송 옵션 조회
        GroupBuyShipping shipping = groupBuyShippingRepository
                .findByIdAndGroupBuyPost_Id(request.shippingId(), post.getId())
                .orElseThrow(() -> new BusinessException(ErrorStatus.GROUP_BUY_SHIPPING_NOT_FOUND));

        DeliveryMethod deliveryMethod = shipping.getDeliveryMethod();
        int shippingPrice = shipping.getPrice();

        // 3 옵션(ex 장원영/ 안유진) id 조회
        List<Long> optionIds = request.items().stream()
                .map(OrderItemRequest::groupBuyOptionId)
                .toList();

        if (optionIds.isEmpty()) {
            throw new BusinessException(ErrorStatus.ORDER_ITEM_EMPTY);
        }

        List<GroupBuyOption> options = groupBuyOptionRepository.findAllByIdIn(optionIds);

        if (options.size() != optionIds.size()) {
            throw new BusinessException(ErrorStatus.GROUP_BUY_OPTION_NOT_FOUND);
        }

        // 4 옵션이 해당 post 소속인지를 검증
        for (GroupBuyOption option : options) {
            if (!option.getGroupBuyPost().getId().equals(post.getId())) {
                throw new BusinessException(ErrorStatus.GROUP_BUY_OPTION_NOT_IN_POST);
            }
        }

        // 5 배송정보
        DeliveryInfo deliveryInfo = new DeliveryInfo(
                request.deliveryInfo().receiverName(),
                request.deliveryInfo().zipcode(),
                request.deliveryInfo().addressLine(),
                request.deliveryInfo().phone()
        );

        // 6 총 금액 계산 = 옵션들 합 + 공구 배송비
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

        // 7 Order 저장
        Order order = Order.create(
                post,
                user,
                deliveryMethod,
                totalAmount,
                deliveryInfo
        );

        Order savedOrder = orderRepository.save(order);

        // 8 OrderItem 저장
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