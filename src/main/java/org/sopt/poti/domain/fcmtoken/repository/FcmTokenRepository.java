package org.sopt.poti.domain.fcmtoken.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.poti.domain.fcmtoken.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  Optional<FcmToken> findByToken(String token);

  List<FcmToken> findAllByUser_Id(Long userId);

  @Transactional
  void deleteAllByUser_Id(Long userId);

  @Transactional
  void deleteByToken(String token);
}
