package com.blaybus.blaybusbe.domain.task.dto.response;

import lombok.Builder;

@Builder
public record TimerStopResponse(
        Long taskId,
        Integer sessionMinutes,
        Integer accumulatedMinutes
) {
}
