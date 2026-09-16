package org.sopt.poti.domain.user.service;

import com.vane.badwordfiltering.BadWordFiltering;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;

@Service
public class ProfanityFilterService {

    private final BadWordFiltering badWordFiltering = new BadWordFiltering();

    public void validateNoProfanity(String text) {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ErrorStatus.INVALID_NICKNAME);
        }

        if (badWordFiltering.blankCheck(text) || badWordFiltering.check(text)) {
            throw new BusinessException(ErrorStatus.PROFANITY_DETECTED);
        }
    }
}
