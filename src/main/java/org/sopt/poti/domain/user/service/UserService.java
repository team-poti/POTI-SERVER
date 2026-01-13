package org.sopt.poti.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    }

    public Optional<User> findUserBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return userRepository.findBySocialIdAndSocialType(socialId, socialType);
    }

    @Transactional
    public User registerUser(User user) {
        return userRepository.save(user);
    }
}
