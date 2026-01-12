package org.sopt.poti.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.dto.request.NicknameDuplicateRequest;
import org.sopt.poti.domain.user.dto.response.NicknameDuplicateResponse;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserNicknameService {

    private static final String NICKNAME_PATTERN = "^[A-Za-z0-9가-힣]{2,10}$";

    private final UserRepository userRepository;
    private final ProfanityFilterService profanityFilterService;

    public NicknameDuplicateResponse checkDuplicate(NicknameDuplicateRequest req) {
        String nickname = normalizeNickname(req.nickname());

        if (nickname == null || !nickname.matches(NICKNAME_PATTERN)) {
            throw new BusinessException(ErrorStatus.INVALID_NICKNAME);
        }

        // 비속어 검사
        profanityFilterService.validateNoProfanity(nickname);

        boolean duplicated = userRepository.existsByNickname(nickname);

        return new NicknameDuplicateResponse(duplicated);
    }

    private String normalizeNickname(String nickname) {
        if (nickname == null) return null;

        return Normalizer.normalize(nickname, Normalizer.Form.NFKC)
                .trim()
                .toLowerCase(Locale.KOREAN);
    }
}
