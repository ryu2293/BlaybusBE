package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.DayOfWeekEnum;
import com.blaybus.blaybusbe.domain.task.enums.Subject;

import java.time.LocalDate;
import java.util.List;

public record CreateMentorTaskRequest(
        Subject subject,
        String title,
        Long weaknessId,

        // 단일 과제: date만 전달
        LocalDate date,

        // 반복 과제: startDate + endDate + daysOfWeek 전달
        LocalDate startDate,
        LocalDate endDate,
        List<DayOfWeekEnum> daysOfWeek
) {
    public boolean isRecurring() {
        return daysOfWeek != null && !daysOfWeek.isEmpty()
                && startDate != null && endDate != null;
    }
}
