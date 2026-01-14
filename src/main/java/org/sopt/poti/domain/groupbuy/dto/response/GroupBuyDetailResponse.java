package org.sopt.poti.domain.groupbuy.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GroupBuyDetailResponse(
    Long postId,
    Boolean isMyPost,
    String status,
    String artist,
    Long artistId,
    String title,
    Integer price, // 인당 가격
    LocalDateTime uploadTime,
    LocalDate deadline,
    List<ImageResponse> images,
    String content,
    List<ShippingResponse> shippingOptions,
    UploaderResponse uploader,
    Integer currentCount,
    Integer totalCount,
    List<ParticipantResponse> participants
) {

  @Builder
  public record ImageResponse(
      Integer sortOrder,
      String imageUrl
  ) {

  }

  @Builder
  public record ShippingResponse(
      Long shippingId,
      String name,
      Integer price
  ) {

  }

  @Builder
  public record UploaderResponse(
      Long userId,
      String nickName,
      String profileImage,
      Double rating,
      Integer reviewCount
  ) {

  }

  @Builder
  public record ParticipantResponse(
      Long userId,
      String nickName,
      String profileImage,
      Double rating,
      List<String> selectedMembers
  ) {

  }
}
