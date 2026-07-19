package org.sopt.poti.domain.fcmtoken.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.fcmtoken.entity.DeviceType;
import org.sopt.poti.domain.fcmtoken.entity.FcmToken;
import org.sopt.poti.domain.fcmtoken.repository.FcmTokenRepository;
import org.sopt.poti.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmTokenService {

  private final FcmTokenRepository fcmTokenRepository;

  @Transactional
  public void register(User user, String token, DeviceType deviceType) {
    fcmTokenRepository.findByToken(token)
        .ifPresentOrElse(
            existing -> existing.reassignUser(user),
            () -> fcmTokenRepository.save(FcmToken.builder()
                .user(user)
                .token(token)
                .deviceType(deviceType)
                .build())
        );
  }

  @Transactional
  public void deleteByToken(String token) {
    fcmTokenRepository.deleteByToken(token);
  }

  @Transactional
  public void deleteAllByUserId(Long userId) {
    fcmTokenRepository.deleteAllByUser_Id(userId);
  }
}
