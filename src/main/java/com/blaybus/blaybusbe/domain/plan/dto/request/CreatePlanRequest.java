package com.blaybus.blaybusbe.domain.plan.dto.request;

import java.time.LocalDate;

public record CreatePlanRequest(
        LocalDate planDate,
        String dailyMemo
) {
}
