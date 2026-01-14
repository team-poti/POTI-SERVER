package org.sopt.poti.domain.participation.dto.response;

import java.time.LocalDateTime;

public record ParticipationListResponse(
        Long participationId,
        Long groupBuyId,
        String artistName,
        String productName,
        String thumbnailUrl,
        String status,
        Integer totalPrice,
        LocalDateTime createdAt
) {}