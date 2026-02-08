package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import com.blaybus.blaybusbe.domain.plan.dto.response.TaskSummaryResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List; // Added
import java.util.Collections; // Added if tasks list is initially empty

@Builder
public record CalendarDayResponse(
        Long planId,
        LocalDate planDate,
        List<TaskSummaryResponse> tasks // Added
) {

    public static CalendarDayResponse from(DailyPlan plan) {
        return CalendarDayResponse.builder()
                .planId(plan.getId())
                .planDate(plan.getPlanDate())
                .tasks(Collections.emptyList()) // Added to initialize tasks list
                .build();
    }
    
    // Overloaded from method to accept tasks list
    public static CalendarDayResponse from(DailyPlan plan, List<TaskSummaryResponse> tasks) {
        return CalendarDayResponse.builder()
                .planId(plan.getId())
                .planDate(plan.getPlanDate())
                .tasks(tasks)
                .build();
    }
}
