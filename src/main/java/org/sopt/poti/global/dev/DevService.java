package org.sopt.poti.global.dev;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

  private final UserRepository userRepository;
  private final GroupBuyRepository groupBuyRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  @Transactional
  public void hardDeleteUser(Long userId) {
    // 1. 유저가 총대로서 올린 게시글과 관련된 '타인의 주문 내역(OrderItem)' 삭제
    List<GroupBuyPost> myPosts = groupBuyRepository.findAllByLeaderId(userId);

    List<Long> myOptionIds = myPosts.stream()
        .flatMap(post -> post.getOptions().stream())
        .map(GroupBuyOption::getId)
        .toList();

    if (!myOptionIds.isEmpty()) {
      orderItemRepository.deleteByGroupBuyOptionIdIn(myOptionIds);
    }

    // 2. 유저가 쓴 게시글 삭제 (Cascade로 옵션, 이미지, 배송정보 삭제됨)
    groupBuyRepository.deleteByLeaderId(userId);

    // 3. 유저의 주문 내역 삭제 (Cascade로 본인의 OrderItem 삭제됨)
    orderRepository.deleteByUser_Id(userId);

    // 4. 유저 삭제
    userRepository.deleteById(userId);
  }
}
