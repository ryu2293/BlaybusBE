package com.blaybus.blaybusbe.domain.task.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RecurringTaskResponse(
        Integer taskCount,
        List<TaskResponse> tasks
) {
}
