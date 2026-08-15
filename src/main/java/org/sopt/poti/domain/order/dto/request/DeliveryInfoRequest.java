package org.sopt.poti.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeliveryInfoRequest(
        @NotBlank(message = "수령인 이름을 입력해주세요.") String receiverName,
        @NotBlank(message = "우편번호를 입력해주세요.") String zipcode,
        @NotBlank(message = "주소를 입력해주세요.") String addressLine,
        @NotBlank(message = "전화번호를 입력해주세요.") String phone
) {}
