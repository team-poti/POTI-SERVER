package org.sopt.poti.domain.user.repository;

import java.util.Optional;
import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);
    boolean existsByNickname(String nickname);
}
