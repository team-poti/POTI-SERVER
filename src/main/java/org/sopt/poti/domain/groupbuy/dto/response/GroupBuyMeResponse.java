package org.sopt.poti.domain.groupbuy.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyMeStatus;

public record GroupBuyMeResponse(
    Integer inProgressCount,  // 진행 중인 분철글 수
    Integer completedCount,   // 종료된 분철글 수
    GroupBuyMeStatus currentStatus, // 현재 필터링 조건
    List<GroupBuyResponse> groupBuyPosts
) {

  public record GroupBuyResponse(
      Long groupBuyId,  // 공구글 식별자
      String artistName,  // 아티스트명
      String productName, // 상품명
      String thumbnailUrl,
      String status,
      LocalDateTime createdAt // 공구글 생성 시간
  ) {

  }
}