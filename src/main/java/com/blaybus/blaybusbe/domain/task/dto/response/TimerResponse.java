package com.blaybus.blaybusbe.domain.task.dto.response;

import com.blaybus.blaybusbe.domain.task.enums.TimerStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimerResponse(
        Long taskId,
        TimerStatus timerStatus,
        LocalDateTime timerStartedAt
) {
}
