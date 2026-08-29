package org.sopt.poti.domain.order.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.sopt.poti.domain.fcmtoken.service.FcmNotificationService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired(required = false)
  private FcmNotificationService fcmNotificationService;

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
        request.deliveryInfo().address(),
        request.deliveryInfo().addressDetail(),
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

    // 8 Order 저장 전에 참여자 주문 고유번호 생성
    String orderNumber;
    do {
      orderNumber = generateOrderNumber();
    } while (orderRepository.existsByOrderNumber(orderNumber));

    // 9 Order 저장
    Order order = Order.create(
        post,
        user,
        deliveryMethod,
        totalAmount,
        deliveryInfo,
        orderNumber
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

    // 10 분철글 현재 인원 증가 및 분철 정원 다 차면 상태 변경(CLOSED)
    int totalCount = request.items().stream()
        .mapToInt(OrderItemRequest::count)
        .sum();
    post.increaseCurrentQuantity(totalCount);

    // 11 "내 배송지로 등록" 체크박스 처리
    userService.saveAddressIfRequested(userId, request.saveAsMyAddress(),
        request.deliveryInfo().receiverName(),
        request.deliveryInfo().zipcode(),
        request.deliveryInfo().address(),
        request.deliveryInfo().addressDetail(),
        request.deliveryInfo().phone());

    if (fcmNotificationService != null) {
      fcmNotificationService.notifyNewParticipant(post, user.getNickname());
      if (post.getStatus() == GroupBuyPostStatus.CLOSED) {
        List<Order> participantOrders = orderRepository.findOrdersWithUserByGroupBuyPost_Id(post.getId());
        fcmNotificationService.notifyPostStatusChanged(post, participantOrders);
      }
    }

    return new CreateOrderResponse(savedOrder.getId());
  }

  //고유 주문번호 생성 관련 메서드
  private String generateOrderNumber() {
    String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
    String randomStr = generateRandomString(4);
    return date + "-" + randomStr;
  }

  private String generateRandomString(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(characters.charAt(random.nextInt(characters.length())));
    }
    return sb.toString();
  }
}