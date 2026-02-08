package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CalendarDayResponse(
        Long planId,
        LocalDate planDate,
        Long totalStudyTime,
        String totalStudyTimeFormatted,
        boolean hasMemo
) {

    public static CalendarDayResponse from(DailyPlan plan) {
        return CalendarDayResponse.builder()
                .planId(plan.getId())
                .planDate(plan.getPlanDate())
                .totalStudyTime(plan.getTotalStudyTime())
                .totalStudyTimeFormatted(TimeUtils.formatSecondsToHHMMSS(plan.getTotalStudyTime()))
                .hasMemo(plan.getDailyMemo() != null && !plan.getDailyMemo().isBlank())
                .build();
    }
}
