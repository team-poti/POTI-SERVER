package org.sopt.poti.domain.user.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);

  boolean existsByNickname(String nickname);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from User u where u.id = :userId")
  java.util.Optional<User> findByIdWithLock(@Param("userId") Long userId);
}
