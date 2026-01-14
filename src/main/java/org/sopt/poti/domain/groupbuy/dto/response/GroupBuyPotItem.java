package org.sopt.poti.domain.groupbuy.dto.response;

import java.util.List;
import lombok.Builder;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;

@Builder
public record GroupBuyPotItem(
    Long potId,
    int price, // 최저가
    String thumbnailUrl,
    int currentCount,   // 현재 참여 인원
    int totalCount,     // 최대 참여 가능 인원
    GroupBuyPostStatus status,
    List<String> availableMembers, // 남은 멤버 이름 리스트
    UploaderInfo uploader
) {

  @Builder
  public record UploaderInfo(
      Long userId,
      String nickname,
      String profileImage,
      double rating
  ) {

  }
}
