package org.sopt.poti.domain.groupbuy.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.QItemImage;
import org.sopt.poti.domain.artist.entity.QArtist;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;

import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;
import static org.sopt.poti.domain.groupbuy.entity.QItemImage.itemImage;
import static org.sopt.poti.domain.artist.entity.QArtist.artist;

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

    @Override
    public List<HomeGroupBuyItem> findPopularTitlesByArtist(Long userId, Long artistId, int limit) {
        QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost"); // Subquery alias
        QGroupBuyPost subGroupBuyPostForImage = new QGroupBuyPost("subGroupBuyPostForImage");
        QItemImage subItemImage = new QItemImage("subItemImage");


        return queryFactory
                .select(Projections.constructor(HomeGroupBuyItem.class,
                        groupBuyPost.title,
                        artist.name,
                        JPAExpressions.select(subItemImage.imageUrl) // Subquery for image (latest post's image for this title)
                                .from(subItemImage)
                                .join(subItemImage.groupBuyPost, subGroupBuyPostForImage)
                                .where(subGroupBuyPostForImage.id.eq(
                                        JPAExpressions.select(subGroupBuyPost.id.max()) // Get latest post ID for title
                                                .from(subGroupBuyPost)
                                                .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                                                        .and(subGroupBuyPost.artist.id.eq(artistId)))
                                ))
                                .limit(1), // Get only one image URL
                        groupBuyPost.id.count(), // postCount (count of posts with this title)
                        groupBuyPost.id.count().when(0L).then("") // Default tag, '인기' tag logic to be implemented later
                                .otherwise("인기").as("tag") // QueryDSL will handle type conversion if HomeGroupBuyItem constructor expects String
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(groupBuyPost.artist.id.eq(artistId))
                .groupBy(groupBuyPost.title, artist.name) // Group by title and artist name
                .orderBy(groupBuyPost.id.count().desc()) // Order by postCount (popular)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long userId, Long artistId, int limit) {
        QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost"); // Subquery alias
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
                                                        .and(subGroupBuyPost.artist.id.ne(artistId))) // Exclude artist
                                ))
                                .limit(1),
                        groupBuyPost.id.count(),
                        groupBuyPost.id.count().when(0L).then("")
                                .otherwise("인기").as("tag")
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(groupBuyPost.artist.id.ne(artistId)) // Exclude artist
                .groupBy(groupBuyPost.title, artist.name)
                .orderBy(groupBuyPost.id.count().desc())
                .limit(limit)
                .fetch();
    }
}
