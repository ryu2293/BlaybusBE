package com.blaybus.blaybusbe.domain.task.dto.response;

import com.blaybus.blaybusbe.domain.task.entity.TaskLog;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskLogResponse(
        Long id,
        Long taskId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Long duration
) {
    public static TaskLogResponse from(TaskLog taskLog) {
        return TaskLogResponse.builder()
                .id(taskLog.getId())
                .taskId(taskLog.getTask().getId())
                .startAt(taskLog.getStartAt())
                .endAt(taskLog.getEndAt())
                .duration(taskLog.getDuration())
                .build();
    }
}
