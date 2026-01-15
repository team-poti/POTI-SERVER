package org.sopt.poti.domain.groupbuy.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;

@Builder
public record GroupBuyPotItem(
    @Schema(description = "분철 팟 게시글 ID", example = "101")
    Long potId,
    @Schema(description = "1인당 가격 (최저가)", example = "21300")
    int price, // 최저가
    @Schema(description = "썸네일 이미지 URL (없으면 null)", example = "https://s3.../101_thumb.jpg")
    String thumbnailUrl,
    @Schema(description = "현재 참여 인원", example = "6")
    int currentCount,   // 현재 참여 인원
    @Schema(description = "총 모집 인원", example = "7")
    int totalCount,     // 최대 참여 가능 인원
    @Schema(description = "모집 상태", example = "RECRUITING")
    GroupBuyPostStatus status,
    @Schema(description = "남은 멤버 이름 리스트", example = "[\"원영\", \"유진\"]")
    List<String> availableMembers, // 남은 멤버 이름 리스트
    @Schema(description = "총대 정보")
    UploaderInfo uploader
) {

  @Builder
  public record UploaderInfo(
      @Schema(description = "총대 유저 ID", example = "55")
      Long userId,
      @Schema(description = "총대 닉네임", example = "분철의악마")
      String nickname,
      @Schema(description = "총대 프로필 이미지 (없으면 null)", example = "https://s3.../55.jpg")
      String profileImage,
      @Schema(description = "총대 평점", example = "4.8")
      double rating
  ) {

  }
}
