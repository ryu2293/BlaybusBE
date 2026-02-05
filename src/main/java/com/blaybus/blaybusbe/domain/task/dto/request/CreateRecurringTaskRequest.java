package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.DayOfWeekEnum;
import com.blaybus.blaybusbe.domain.task.enums.Subject;

import java.time.LocalDate;
import java.util.List;

public record CreateRecurringTaskRequest(
        LocalDate startDate,
        LocalDate endDate,
        List<DayOfWeekEnum> daysOfWeek,
        Subject subject,
        String title,
        String goal,
        String description,
        Long weaknessId,
        Boolean isMandatory
) {
}
