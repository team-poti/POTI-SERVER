package org.sopt.poti.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeliveryInfoRequest(
        @NotBlank(message = "수령인 이름을 입력해주세요.") String receiverName,
        @NotBlank(message = "우편번호를 입력해주세요.") String zipcode,
        @NotBlank(message = "주소를 입력해주세요.") @Size(max = 255) String address,
        @Size(max = 255) String addressDetail,
        @NotBlank(message = "전화번호를 입력해주세요.") String phone
) {}
