package org.sopt.poti.domain.user.repository;

import java.util.Optional;
import org.sopt.poti.domain.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

  Optional<UserAddress> findByUserId(Long userId);
}
