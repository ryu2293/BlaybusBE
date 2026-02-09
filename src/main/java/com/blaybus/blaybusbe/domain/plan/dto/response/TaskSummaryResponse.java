package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import lombok.Builder;

@Builder
public record TaskSummaryResponse(
        Long taskId,
        String title,
        Subject subject,
        TaskStatus status,
        String actualStudyTimeFormatted
) {
    public static TaskSummaryResponse from(Task task) {
        return TaskSummaryResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .subject(task.getSubject())
                .status(task.getStatus())
                .actualStudyTimeFormatted(TimeUtils.formatSecondsToHHMMSS(task.getActualStudyTime()))
                .build();
    }
}
