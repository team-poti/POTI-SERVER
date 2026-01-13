package org.sopt.poti.domain.groupbuy.repository;

import static org.sopt.poti.domain.artist.entity.QArtist.artist;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.QItemImage;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;

@RequiredArgsConstructor
public class GroupBuyRepositoryImpl implements GroupBuyRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findTitlesByKeyword(Long artistId, String keyword, int limit) {
    return queryFactory
        .selectDistinct(groupBuyPost.title)
        .from(groupBuyPost)
        .where(
            groupBuyPost.artist.id.eq(artistId),
            groupBuyPost.title.contains(keyword)
        )
        .limit(limit)
        .fetch();
  }

  @Override
  public List<HomeGroupBuyItem> findPopularTitlesByArtist(Long artistId, int limit) {
    QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");
    QGroupBuyPost subGroupBuyPostForImage = new QGroupBuyPost("subGroupBuyPostForImage");
    QItemImage subItemImage = new QItemImage("subItemImage");

    return queryFactory
        .select(Projections.constructor(HomeGroupBuyItem.class,
            groupBuyPost.title,
            artist.name,
            JPAExpressions.select(subItemImage.imageUrl)
                .from(subItemImage)
                .join(subItemImage.groupBuyPost, subGroupBuyPostForImage)
                .where(subGroupBuyPostForImage.id.eq(
                    JPAExpressions.select(subGroupBuyPost.id.max())
                        .from(subGroupBuyPost)
                        .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                            .and(subGroupBuyPost.artist.id.eq(artistId))
                            .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                ))
                .orderBy(subItemImage.sortOrder.asc()) // sortOrder 정렬 추가
                .limit(1), groupBuyPost.id.count(),
            groupBuyPost.id.count().when(0L).then("")
                .otherwise("인기").as("tag")
        ))
        .from(groupBuyPost)
        .join(groupBuyPost.artist, artist)
        .where(groupBuyPost.artist.id.eq(artistId))
        .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
        .orderBy(groupBuyPost.id.count().desc())
        .limit(limit)
        .fetch();
  }

  @Override
  public List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long artistId, int limit) {
    QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");
    QGroupBuyPost subGroupBuyPostForImage = new QGroupBuyPost("subGroupBuyPostForImage");
    QItemImage subItemImage = new QItemImage("subItemImage");

    return queryFactory
        .select(Projections.constructor(HomeGroupBuyItem.class,
            groupBuyPost.title,
            artist.name,
            JPAExpressions.select(subItemImage.imageUrl)
                .from(subItemImage)
                .join(subItemImage.groupBuyPost, subGroupBuyPostForImage)
                .where(subGroupBuyPostForImage.id.eq(
                    JPAExpressions.select(subGroupBuyPost.id.max())
                        .from(subGroupBuyPost)
                        .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                            .and(artistIdNe(artistId, subGroupBuyPost))
                            .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                ))
                .orderBy(subItemImage.sortOrder.asc())
                .limit(1),
            groupBuyPost.id.count(),
            groupBuyPost.id.count().when(0L).then("")
                .otherwise("인기").as("tag")
        ))
        .from(groupBuyPost)
        .join(groupBuyPost.artist, artist)
        .where(artistIdNe(artistId, groupBuyPost))
        .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
        .orderBy(groupBuyPost.id.count().desc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression artistIdNe(Long artistId, QGroupBuyPost post) {
    return artistId != null ? post.artist.id.ne(artistId) : null;
  }
}