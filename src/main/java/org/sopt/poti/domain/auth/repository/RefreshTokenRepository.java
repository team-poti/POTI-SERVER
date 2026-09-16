package org.sopt.poti.domain.auth.repository;

import org.sopt.poti.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    
}
