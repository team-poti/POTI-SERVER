package org.sopt.poti.domain.fcmtoken;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.poti.domain.fcmtoken.entity.DeviceType;
import org.sopt.poti.domain.fcmtoken.entity.FcmToken;
import org.sopt.poti.domain.fcmtoken.repository.FcmTokenRepository;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FcmToken afterCommit 삭제")
class FcmTokenDeleteAfterCommitTest {

  @Autowired FcmTokenRepository fcmTokenRepository;
  @Autowired UserRepository userRepository;
  @Autowired TransactionTemplate transactionTemplate;

  @Test
  @DisplayName("afterCommit 콜백 안에서 deleteByToken이 실제로 반영된다")
  void deleteByToken_afterCommit에서_실제로_삭제된다() throws InterruptedException {
    // given
    User user = transactionTemplate.execute(status ->
        userRepository.save(User.createSocialUser(
            "test-social-id", SocialType.KAKAO, "test@test.com", "테스터", null, null)));

    FcmToken saved = transactionTemplate.execute(status ->
        fcmTokenRepository.save(FcmToken.builder()
            .user(user)
            .token("test-fcm-token-" + System.currentTimeMillis())
            .deviceType(DeviceType.IOS)
            .build()));

    // when - afterCommit 콜백에서 deleteByToken 호출 (REQUIRES_NEW 검증)
    CountDownLatch latch = new CountDownLatch(1);
    transactionTemplate.execute(status -> {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          fcmTokenRepository.deleteByToken(saved.getToken());
          latch.countDown();
        }
      });
      return null;
    });
    latch.await(3, TimeUnit.SECONDS);

    // then
    assertThat(fcmTokenRepository.findByToken(saved.getToken())).isEmpty();

    // cleanup
    transactionTemplate.execute(status -> {
      userRepository.delete(user);
      return null;
    });
  }
}
