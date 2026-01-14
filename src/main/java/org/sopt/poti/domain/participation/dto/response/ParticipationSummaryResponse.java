package org.sopt.poti.domain.participation.dto.response;

import java.util.List;

public record ParticipationSummaryResponse(
        int inProgressCount,
        int completedCount,
        List<ParticipationListResponse> participations
) {}
