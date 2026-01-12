package org.sopt.poti.domain.groupbuy.repository;

import static org.sopt.poti.domain.artist.entity.QArtist.artist;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;

import com.querydsl.core.types.Projections;
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
    QGroupBuyPost groupBuyPost = QGroupBuyPost.groupBuyPost;

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

import com.querydsl.core.types.dsl.BooleanExpression; // Import 추가

// ... (기존 imports)

    @Override
    public List<HomeGroupBuyItem> findPopularTitlesByArtist(Long userId, Long artistId, int limit) {
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
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id))) // 아티스트 일치 조건 추가 (명시적)
                                ))
                                .limit(1),
                        groupBuyPost.id.count(),
                        groupBuyPost.id.count().when(0L).then("")
                                .otherwise("인기").as("tag")
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(groupBuyPost.artist.id.eq(artistId))
                .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id) // artist.id 그룹핑 추가
                .orderBy(groupBuyPost.id.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long userId, Long artistId, int limit) {
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
                                                        .and(artistIdNe(artistId, subGroupBuyPost)) // 서브쿼리에도 제외 조건 적용? 아니면 같은 아티스트?
                                                        // 서브쿼리는 "해당 그룹(Title, Artist)"의 최신 글을 찾는 것이므로
                                                        // groupBuyPost.artist.id와 일치해야 함.
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id))) 
                                ))
                                .limit(1),
                        groupBuyPost.id.count(),
                        groupBuyPost.id.count().when(0L).then("")
                                .otherwise("인기").as("tag")
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(artistIdNe(artistId, groupBuyPost)) // 동적 쿼리 적용
                .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
                .orderBy(groupBuyPost.id.count().desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression artistIdNe(Long artistId, QGroupBuyPost post) {
        return artistId != null ? post.artist.id.ne(artistId) : null;
    }
}
