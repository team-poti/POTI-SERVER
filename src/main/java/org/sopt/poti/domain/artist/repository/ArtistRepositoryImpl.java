package org.sopt.poti.domain.artist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.QArtist;

@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findNamesByPrefix(String keyword, int limit) {
    QArtist artist = QArtist.artist;

    return queryFactory
        .selectDistinct(artist.name)
        .from(artist)
        .where(artist.name.startsWithIgnoreCase(keyword))
        .orderBy(artist.name.asc())
        .limit(limit)
        .fetch();
  }
}
