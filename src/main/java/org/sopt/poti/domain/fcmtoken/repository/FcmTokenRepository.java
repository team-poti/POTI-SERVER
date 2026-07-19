package org.sopt.poti.domain.fcmtoken.repository;

import java.util.Optional;
import org.sopt.poti.domain.fcmtoken.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  Optional<FcmToken> findByToken(String token);

  void deleteByToken(String token);

  void deleteAllByUser_Id(Long userId);
}
