package org.sopt.poti.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활동 중"),
    WITHDRAWN("탈퇴");

    private final String description;
}
