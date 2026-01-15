package org.sopt.poti.domain.groupbuy.repository;

import static org.sopt.poti.domain.artist.entity.QArtist.artist;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyOption.groupBuyOption;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;
import static org.sopt.poti.domain.groupbuy.entity.QItemImage.itemImage;
import static org.sopt.poti.domain.order.entity.QOrderItem.orderItem;

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
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyListRequest;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.QItemImage;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;
import org.sopt.poti.domain.order.entity.QOrderItem;
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
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl)
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
    public List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long artistId, String sort, int limit) {
        QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");

        return queryFactory
                .select(Projections.constructor(HomeGroupBuyItem.class,
                        groupBuyPost.title,
                        artist.name,
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl)
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
                .orderBy(sortCondition(sort))
                .limit(limit)
                .fetch();
    }

    @Override
    public Slice<FeedGroupItem> findFeedItems(FeedSearchCondition condition, Pageable pageable) {
        QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");

        List<FeedGroupItem> content = queryFactory
                .select(Projections.constructor(FeedGroupItem.class,
                        artist.name,
                        JPAExpressions.select(subGroupBuyPost.representativeImageUrl)
                                .from(subGroupBuyPost)
                                .where(subGroupBuyPost.id.eq(
                                        JPAExpressions.select(subGroupBuyPost.id.max())
                                                .from(subGroupBuyPost)
                                                .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                                                        .and(artistIdEq(condition.artistId(), subGroupBuyPost))
                                                        .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                                )),
                        groupBuyPost.title,
                        groupBuyPost.id.count(),
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

    @Override
    public Slice<GroupBuyPost> findGroupBuyList(GroupBuyListRequest request, Pageable pageable) {
        List<GroupBuyPost> content = queryFactory
                .selectFrom(groupBuyPost)
                .join(groupBuyPost.artist, artist).fetchJoin() // Artist Fetch Join
                .join(groupBuyPost.leader).fetchJoin()         // Leader Fetch Join
                .where(
                        groupBuyPost.title.eq(request.title()),
                        groupBuyPost.artist.id.eq(request.artistId()),
                        memberIdsIn(request.memberIds()) // 멤버 필터링
                )
                .orderBy(listSortCondition(request.sort()))
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

    // 멤버 필터링: 선택한 멤버들이 '남아있는(주문되지 않은)' 상태여야 함
    private BooleanExpression memberIdsIn(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return null;
        }

        // 서브쿼리: 이 게시글의 옵션 중에, memberId가 일치하면서, OrderItem이 없는(주문 안 된) 옵션이 존재하는가?
        return JPAExpressions.selectOne()
                .from(groupBuyOption)
                .leftJoin(orderItem).on(orderItem.groupBuyOption.eq(groupBuyOption))
                .where(
                        groupBuyOption.groupBuyPost.eq(groupBuyPost),
                        groupBuyOption.member.id.in(memberIds),
                        orderItem.isNull() // 주문 내역이 없어야 함 (남은 멤버)
                )
                .exists();
    }

    private OrderSpecifier<?> listSortCondition(String sort) {
        if ("DEADLINE".equalsIgnoreCase(sort)) {
            return groupBuyPost.recruitDeadline.asc();
        }
        if ("RATING".equalsIgnoreCase(sort)) {
            return groupBuyPost.leader.ratingAvg.desc();
        }
        // 기본값: 최신순
        return groupBuyPost.createdAt.desc();
    }

    private BooleanExpression artistIdNe(Long artistId, QGroupBuyPost post) {
        return artistId != null ? post.artist.id.ne(artistId) : null;
    }

    private BooleanExpression artistIdEq(Long artistId, QGroupBuyPost post) {
        return artistId != null ? post.artist.id.eq(artistId) : null;
    }

    private OrderSpecifier<?> sortCondition(String sort) {
        if ("HOT".equalsIgnoreCase(sort)) {
            return groupBuyPost.id.count().desc();
        }
        if ("RANDOM".equalsIgnoreCase(sort)) {
            LocalDate today = LocalDate.now();
            int seed = Integer.parseInt(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            return Expressions.numberTemplate(Double.class, "function('rand', {0})", seed).asc();
        }
        return groupBuyPost.createdAt.max().desc();
    }
}