package org.sopt.poti.domain.artist.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse.ArtistTitleDto;
import org.sopt.poti.domain.artist.entity.QArtist;

@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ArtistTitleDto> findByKeyword(String keyword, int limit) {
    QArtist artist = QArtist.artist;

    return queryFactory
        .selectDistinct(
            Projections.constructor(
                ArtistTitleDto.class,
                artist.id,
                artist.name
            )
        )
        .from(artist)
        .where(
            artist.name.containsIgnoreCase(keyword)
        )
        .orderBy(
            new CaseBuilder()
                .when(artist.name.startsWithIgnoreCase(keyword)).then(0)
                .otherwise(1)
                .asc(),
            artist.name.asc()
        )
        .limit(limit)
        .fetch();
  }

}
