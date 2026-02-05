package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.DayOfWeekEnum;
import com.blaybus.blaybusbe.domain.task.enums.Subject;

import java.time.LocalDate;

public record CreateMentorTaskRequest(
        LocalDate date,
        String title,
        Subject subject,
        String goal,
        String description,
        Integer weekNumber,
        DayOfWeekEnum dayOfWeek,
        Long weaknessId,
        Long studyMaterialId,
        Boolean isMandatory
) {
}
