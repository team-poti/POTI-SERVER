package org.sopt.poti.domain.groupbuy.repository;

import static org.sopt.poti.domain.artist.entity.QArtist.artist;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.feed.dto.request.FeedSearchCondition;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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

        return queryFactory
                .select(Projections.constructor(HomeGroupBuyItem.class,
                        groupBuyPost.title,
                        artist.name,
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl) // 최신 게시글의 대표 이미지 (역정규화)
                                .from(subGroupBuyPost)
                                .where(subGroupBuyPost.id.eq(
                                        JPAExpressions.select(subGroupBuyPost.id.max())
                                                .from(subGroupBuyPost)
                                                .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                                                        .and(subGroupBuyPost.artist.id.eq(artistId))
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                                )),
                        groupBuyPost.id.count(),
                        new CaseBuilder()
                                .when(groupBuyPost.id.count().goe(5L)).then("인기")
                                .otherwise("").as("tag")
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

        return queryFactory
                .select(Projections.constructor(HomeGroupBuyItem.class,
                        groupBuyPost.title,
                        artist.name,
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl) // 최신 게시글의 대표 이미지 (역정규화)
                                .from(subGroupBuyPost)
                                .where(subGroupBuyPost.id.eq(
                                        JPAExpressions.select(subGroupBuyPost.id.max())
                                                .from(subGroupBuyPost)
                                                .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                                                        .and(artistIdNe(artistId, subGroupBuyPost))
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                                )),
                        groupBuyPost.id.count(),
                        new CaseBuilder()
                                .when(groupBuyPost.id.count().goe(5L)).then("인기")
                                .otherwise("").as("tag")
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(artistIdNe(artistId, groupBuyPost))
                .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
                .orderBy(groupBuyPost.id.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Slice<FeedGroupItem> findFeedItems(FeedSearchCondition condition, Pageable pageable) {
        QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");

        List<FeedGroupItem> content = queryFactory
                .select(Projections.constructor(FeedGroupItem.class,
                        artist.name,
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl) // 최신 게시글의 대표 이미지 (역정규화)
                                .from(subGroupBuyPost)
                                .where(subGroupBuyPost.id.eq(
                                        JPAExpressions.select(subGroupBuyPost.id.max())
                                                .from(subGroupBuyPost)
                                                .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                                                        .and(artistIdEq(condition.artistId(), subGroupBuyPost))
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                                )),
                        groupBuyPost.title,
                        groupBuyPost.id.count(), // postCount
                        new CaseBuilder()
                                .when(groupBuyPost.id.count().goe(5L)).then("인기")
                                .otherwise("").as("tag")
                ))
                .from(groupBuyPost)
                .join(groupBuyPost.artist, artist)
                .where(artistIdEq(condition.artistId(), groupBuyPost))
                .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
                .orderBy(sortCondition(condition.sort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression artistIdNe(Long artistId, QGroupBuyPost post) {
        return artistId != null ? post.artist.id.ne(artistId) : null;
    }

    private BooleanExpression artistIdEq(Long artistId, QGroupBuyPost post) {
        return artistId != null ? post.artist.id.eq(artistId) : null;
    }

    private OrderSpecifier<?> sortCondition(String sort) {
        if ("HOT".equalsIgnoreCase(sort)) {
            // 인기순: 게시글 많은 순
            return groupBuyPost.id.count().desc();
        }
        if ("RANDOM".equalsIgnoreCase(sort)) {
            // MySQL RAND()
            LocalDate today = LocalDate.now();
            int seed = Integer.parseInt(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            return Expressions.numberTemplate(Double.class, "function('rand', {0})", seed).asc();
        }
        // 기본값: 최신순 (그룹 내 가장 최신 글 기준)
        return groupBuyPost.createdAt.max().desc();
    }
}