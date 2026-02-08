package com.blaybus.blaybusbe.global.common.util;

public class TimeUtils {

    public static String formatSecondsToHHMMSS(Long totalSeconds) {
        if (totalSeconds == null || totalSeconds < 0) {
            return "00:00:00";
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
