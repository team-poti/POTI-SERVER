package org.sopt.poti.domain.groupbuy.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public record GroupBuyCreateRequest(
    @NotNull(message = "아티스트 ID는 필수입니다.")
    Long artistId,

    @NotBlank(message = "공구 제목은 필수입니다.")
    String title,

    @NotBlank(message = "상세 설명은 필수입니다.")
    String content,

    @NotNull(message = "모집 마감일은 필수입니다.")
    @FutureOrPresent(message = "모집 마감일은 현재 또는 미래여야 합니다.")
    LocalDate deadline, // yyyy-MM-dd 형식

    @NotBlank(message = "은행명은 필수입니다.")
    String bankName,

    @NotBlank(message = "계좌번호는 필수입니다.")
    String accountNumber,

    @NotEmpty(message = "이미지 경로는 최소 1개 이상 필수입니다.")
    List<String> imageUrls, // S3 키값 리스트

    @NotEmpty(message = "멤버 옵션은 최소 1개 이상 필수입니다.")
    @Valid
    List<OptionRequest> options,

    @NotEmpty(message = "배송 방법은 최소 1개 이상 필수입니다.")
    @Valid
    List<ShippingRequest> shippings
) {

  public record OptionRequest(
      @NotNull(message = "멤버 ID는 필수입니다.")
      Long memberId,

      @Positive(message = "가격은 양수여야 합니다.")
      int price
  ) {

  }

  public record ShippingRequest(
      @NotNull(message = "배송 방법 ID는 필수입니다.")
      Long deliveryMethodId,

      @Positive(message = "배송비는 양수여야 합니다.")
      int price
  ) {

  }
}
