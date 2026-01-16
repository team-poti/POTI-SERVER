package org.sopt.poti.domain.groupbuy.dto.response;

import java.util.List;
import lombok.Builder;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.OrderStatus;

@Builder
public record GroupBuySaleDetailResponse(
    Long postId,
    Integer totalCount,
    String imageUrl,
    String artistName,
    String title,
    GroupBuyPostStatus postStatus,
    String statusMessage,
    List<Participant> participant
) {

  public record Participant(
      Long orderId,
      Long userId,
      List<String> memberNames,
      OrderStatus status,
      PriceInfoForDetail priceInfo,
      ShippingInfoForDetail shippingInfo
  ) {

  }

  public record PriceInfoForDetail(
      String shippingName,  // 배송 방법
      Integer totalPrice  // 멤버 옵션 + 배송 총 금액
  ) {

  }

  public record ShippingInfoForDetail(
      String receiverName,  // 수신자 이름
      String address, // 배송지
      String phone  // 폰번호
  ) {

  }
}
