package org.sopt.poti.domain.groupbuy.dto.response;

import java.util.List;

public record GroupBuyTitlesResponse(
    List<String> titles
) {
    public static GroupBuyTitlesResponse of(List<String> titles) {
        return new GroupBuyTitlesResponse(titles);
    }
}
