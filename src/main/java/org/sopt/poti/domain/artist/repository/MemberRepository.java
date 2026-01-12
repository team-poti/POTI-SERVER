package org.sopt.poti.domain.artist.repository;

import org.sopt.poti.domain.artist.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
