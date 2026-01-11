package org.sopt.poti.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NicknameDuplicateRequest(
        @NotBlank
        @Size(min = 2, max = 10)
        String nickname
) {}