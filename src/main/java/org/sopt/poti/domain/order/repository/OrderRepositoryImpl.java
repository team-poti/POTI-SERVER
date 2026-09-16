package org.sopt.poti.domain.order.repository;

import static org.sopt.poti.domain.delivery.entity.QDeliveryMethod.deliveryMethod;
import static org.sopt.poti.domain.order.entity.QOrder.order;
import static org.sopt.poti.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.order.entity.Order;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Order> findAllOrdersWithBasicInfo(Long postId) {

    return queryFactory
        .selectFrom(order)
        // User와 DeliveryMethod를 한 방에 가져옴 (Fetch Join)
        .join(order.user, user).fetchJoin()
        .join(order.deliveryMethod, deliveryMethod).fetchJoin()

        // 조건: 해당 분철글(postId)의 주문만
        .where(order.groupBuyPost.id.eq(postId))

        // 정렬: 최신순
        .orderBy(order.createdAt.desc())
        .fetch();
  }

}
