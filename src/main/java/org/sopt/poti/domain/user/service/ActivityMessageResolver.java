package org.sopt.poti.domain.user.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ActivityMessageResolver {

    public String resolve(LocalDateTime lastActiveAt) {
        if (lastActiveAt == null) return "최근 한 달 이내 활동하지 않음";

        long days = ChronoUnit.DAYS.between(
                lastActiveAt.toLocalDate(),
                LocalDate.now()
        );

        if (days <= 3) return "최근 3일 이내 활동";
        if (days <= 7) return "최근 일주일 이내 활동";
        if (days <= 14) return "최근 2주 이내 활동";
        if (days <= 21) return "최근 3주 이내 활동";
        if (days <= 30) return "최근 한 달 이내 활동";
        return "최근 한 달 이내 활동하지 않음";
    }
}
