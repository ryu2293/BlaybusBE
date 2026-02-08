package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DailyPlanTaskResponse(
        Long id,
        String title,
        Subject subject,
        Boolean isMentorChecked,
        String actualStudyTimeFormatted,
        Boolean isMandatory,
        LocalDate taskDate
) {
    public static DailyPlanTaskResponse from(Task task) {
        return DailyPlanTaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .subject(task.getSubject())
                .isMentorChecked(task.getIsMentorChecked())
                .actualStudyTimeFormatted(TimeUtils.formatSecondsToHHMMSS(task.getActualStudyTime()))
                .isMandatory(task.getIsMandatory())
                .taskDate(task.getTaskDate())
                .build();
    }
}
