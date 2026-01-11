package org.sopt.poti.domain.groupbuy.repository;

import java.util.List;

public interface GroupBuyRepositoryCustom {
    List<String> findTitlesByKeyword(Long artistId, String keyword, int limit);
}
