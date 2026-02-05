package com.blaybus.blaybusbe.domain.weeklyReport.dto.response;

import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;

import java.time.LocalDate;

public record ResponseWeeklyReportDto(
        Long reportId,
        Integer reportYear,
        Integer reportMonth,
        Integer weekNumber,
        LocalDate startDate,
        LocalDate endDate,
        String overallFeedback,
        String strengths,
        String weaknesses
) {
    public static ResponseWeeklyReportDto from(WeeklyReport report) {
        return new ResponseWeeklyReportDto(
                report.getId(),
                report.getReportYear(),
                report.getReportMonth(),
                report.getWeekNumber(),
                report.getStartDate(),
                report.getEndDate(),
                report.getOverallFeedback(),
                report.getStrengths(),
                report.getWeaknesses()
        );
    }
}