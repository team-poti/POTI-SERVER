package org.sopt.poti.domain.artist.repository;

import java.util.List;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  List<Member> findByArtist(Artist artist);

  void deleteByArtist(Artist artist);
}
