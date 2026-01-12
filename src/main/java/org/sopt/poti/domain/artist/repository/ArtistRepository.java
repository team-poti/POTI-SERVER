package org.sopt.poti.domain.artist.repository;

import org.sopt.poti.domain.artist.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository  extends JpaRepository <Artist, Long> {

}
