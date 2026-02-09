package com.blaybus.blaybusbe.domain.task.dto.response;

import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import lombok.Builder;

@Builder
public record TimerStopResponse(
        Long taskId,
        Long sessionSeconds,
        String sessionFormatted,
        Long accumulatedSeconds,
        String accumulatedFormatted
) {

    public static String formatTime(Long totalSeconds) {
        return TimeUtils.formatSecondsToHHMMSS(totalSeconds);
    }
}
