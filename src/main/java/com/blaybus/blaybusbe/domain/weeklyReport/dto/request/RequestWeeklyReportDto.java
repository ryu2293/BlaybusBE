package com.blaybus.blaybusbe.domain.weeklyReport.dto.request;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RequestWeeklyReportDto(
        @NotNull
        Long menteeId,
        @NotNull
        Integer reportYear,
        @NotNull
        Integer reportMonth,
        @NotNull
        Integer weekNumber, // 몇 주차 Ex) 1주차
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,

        String overallFeedback,       // 멘토 총평
        String strengths,             // 잘한 점
        String weaknesses             // 보완할 점
) {
    /**
     * 주간 레포트 생성
     *
     * @param menteeInfo 멘토-멘티 관계
     * @return 주간 레포트
     */
    public WeeklyReport dtoToEntity(MenteeInfo menteeInfo) {
        return WeeklyReport.builder()
                .year(this.reportYear )
                .month(this.reportMonth())
                .weekNumber(this.weekNumber())
                .startDate(this.startDate())
                .endDate(this.endDate())
                .overallFeedback(this.overallFeedback())
                .strengths(this.strengths())
                .weaknesses(this.weaknesses())
                .menteeInfo(menteeInfo)
                .build();
    }
}