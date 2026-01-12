package org.sopt.poti.domain.groupbuy.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.groupbuy.entity.QGroupBuyPost;

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
}
