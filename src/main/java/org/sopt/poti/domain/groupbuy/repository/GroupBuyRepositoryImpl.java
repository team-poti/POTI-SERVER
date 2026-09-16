package org.sopt.poti.domain.groupbuy.repository;

import static org.sopt.poti.domain.artist.entity.QArtist.artist;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyOption.groupBuyOption;
import static org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost.groupBuyPost;
import static org.sopt.poti.domain.order.entity.QOrderItem.orderItem;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.feed.dto.request.FeedSearchCondition;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyListRequest;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Slf4j
@RequiredArgsConstructor
public class GroupBuyRepositoryImpl implements GroupBuyRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EntityManager em;

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
            artist.id,
            JPAExpressions.select(subGroupBuyPost.representativeImageUrl)
                .from(subGroupBuyPost)
                .where(subGroupBuyPost.id.eq(
                    JPAExpressions.select(subGroupBuyPost.id.max())
                        .from(subGroupBuyPost)
                        .where(subGroupBuyPost.title.eq(groupBuyPost.title)
                            .and(artistIdEq(artistId, subGroupBuyPost))
                            .and(subGroupBuyPost.artist.id.eq(groupBuyPost.artist.id)))
                )),
            groupBuyPost.id.count(),
            new CaseBuilder()
                .when(groupBuyPost.id.count().goe(5L)).then("인기")
                .otherwise("").as("tag")
        ))
        .from(groupBuyPost)
        .join(groupBuyPost.artist, artist)
        .where(artistIdEq(artistId, groupBuyPost))
        .groupBy(groupBuyPost.title, artist.name, groupBuyPost.artist.id)
        .orderBy(groupBuyPost.id.count().desc())
        .limit(limit)
        .fetch();
  }

  @Override
  public List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long artistId, String sort,
      int limit) {
    QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");

    return queryFactory
        .select(Projections.constructor(HomeGroupBuyItem.class,
            groupBuyPost.title,
            artist.name,
            artist.id,
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
            artist.id,
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

  @Override
  public List<String> findTitlesByNgram(Long artistId, String keyword, int limit) {
    String searchKeyword = sanitizeForFulltext(keyword);
    if (searchKeyword == null) {
      return Collections.emptyList();
    }

    String sql = "SELECT DISTINCT title FROM group_buy_posts " +
        "WHERE artist_id = :artistId " +
        "AND MATCH(title) AGAINST(:keyword IN BOOLEAN MODE) " +
        "LIMIT :limit";

    return em.createNativeQuery(sql)
        .setParameter("artistId", artistId)
        .setParameter("keyword", searchKeyword)
        .setParameter("limit", limit)
        .getResultList();
  }

  @Override
  public List<String> findTitlesByNgramGlobal(String keyword, int limit) {
    String searchKeyword = sanitizeForFulltext(keyword);
    if (searchKeyword == null) {
      return Collections.emptyList();
    }

    String sql = "SELECT DISTINCT title FROM group_buy_posts " +
        "WHERE MATCH(title) AGAINST(:keyword IN BOOLEAN MODE) " +
        "LIMIT :limit";

    return em.createNativeQuery(sql)
        .setParameter("keyword", searchKeyword)
        .setParameter("limit", limit)
        .getResultList();
  }

  // MySQL Boolean Mode 연산자 제거 후 prefix 검색 형태로 변환 ("공구 뉴진스" → "+공구* +뉴진스*")
  private String sanitizeForFulltext(String keyword) {
    String sanitized = keyword.replaceAll("[+\\-<>~*()\"@]", " ").trim();
    if (sanitized.isBlank()) {
      return null;
    }
    return Arrays.stream(sanitized.split("\\s+"))
        .filter(w -> !w.isBlank())
        .map(w -> "+" + w + "*")
        .collect(Collectors.joining(" "));
  }

  @Override
  public Slice<FeedGroupItem> searchByKeyword(String keyword, Pageable pageable) {
    QGroupBuyPost subGroupBuyPost = new QGroupBuyPost("subGroupBuyPost");

    BooleanExpression titleMatch = groupBuyPost.title.containsIgnoreCase(keyword);
    BooleanExpression artistMatch = artist.name.containsIgnoreCase(keyword);

    List<FeedGroupItem> content = queryFactory
        .select(Projections.constructor(FeedGroupItem.class,
            artist.name,
            artist.id,
            JPAExpressions.select(subGroupBuyPost.representativeImageUrl)
                .from(subGroupBuyPost)
                .where(subGroupBuyPost.id.eq(
                    JPAExpressions.select(subGroupBuyPost.id.max())
                        .from(subGroupBuyPost)
                        .where(subGroupBuyPost.title.eq(groupBuyPost.title)
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
        .where(titleMatch.or(artistMatch))
        .groupBy(groupBuyPost.title, artist.name, artist.id)
        .orderBy(groupBuyPost.createdAt.max().desc())
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
    return JPAExpressions.select(groupBuyOption.count()) // selectOne -> count
        .from(groupBuyOption)
        .leftJoin(orderItem).on(orderItem.groupBuyOption.eq(groupBuyOption))
        .where(
            groupBuyOption.groupBuyPost.eq(groupBuyPost),
            groupBuyOption.member.id.in(memberIds),
            orderItem.isNull() // 주문 내역이 없어야 함 (남은 멤버)
        )
        .eq((long) memberIds.size());
  }

  private OrderSpecifier<?>[] listSortCondition(String sort) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    // 1순위: 모집 중인 글 우선 (RECRUITING = 1, 나머지 = 2)
    orders.add(new CaseBuilder()
        .when(groupBuyPost.status.eq(org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus.RECRUITING)).then(1)
        .otherwise(2)
        .asc());

    // 2순위: 사용자 선택 정렬
    if ("DEADLINE".equalsIgnoreCase(sort)) {
      orders.add(groupBuyPost.recruitDeadline.asc());
    } else if ("RATING".equalsIgnoreCase(sort)) {
      orders.add(groupBuyPost.leader.ratingAvg.desc());
    }
    
    // 3순위 (기본): 최신순
    orders.add(groupBuyPost.createdAt.desc());

    return orders.toArray(new OrderSpecifier[0]);
  }

  private BooleanExpression artistIdNe(Long artistId, QGroupBuyPost post) {
    return artistId != null ? post.artist.id.ne(artistId) : null;
  }

  private BooleanExpression artistIdEq(Long artistId, QGroupBuyPost post) {
    return artistId != null ? post.artist.id.eq(artistId) : null;
  }

  private OrderSpecifier<?>[] sortCondition(String sort) {
    log.info("sort :{}", sort);
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    if ("HOT".equalsIgnoreCase(sort)) {
      log.info("HOT SORT");
      orders.add(groupBuyPost.id.count().desc()); // 1순위: 인기순 (게시글 수)
      orders.add(groupBuyPost.createdAt.max().desc()); // 2순위: 최신순 (동점 처리)
    } else if ("RANDOM".equalsIgnoreCase(sort)) {
      log.info("RANDOM SORT");
      LocalDate today = LocalDate.now();
      int seed = Integer.parseInt(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
      orders.add(Expressions.numberTemplate(Double.class, "function('rand', {0})", seed).asc());
      orders.add(groupBuyPost.createdAt.max().desc()); // 랜덤 정렬 시에도 최신순을 2차 기준으로
    } else { // LATEST (기본값)
      log.info("LATEST SORT");
      orders.add(groupBuyPost.createdAt.max().desc());
    }

    // HOT이 기본 정렬이므로, sort 파라미터가 없거나 인식되지 않으면 HOT 로직을 타도록 한다.
    // 하지만 현재 컨트롤러에서 defaultValue="HOT"으로 명시되어 있으므로 이 else 분기는 LATEST만 처리함.

    return orders.toArray(new OrderSpecifier[0]);
  }
}