package com.blaybus.blaybusbe.domain.task.dto.response;

import lombok.Builder;

@Builder
public record TimerStopResponse(
        Long taskId,
        Long sessionSeconds,
        Long accumulatedSeconds
) {
}
