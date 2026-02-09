package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.DayOfWeekEnum;
import com.blaybus.blaybusbe.domain.task.enums.Subject;

import java.time.LocalDate;
import java.util.List;

public record CreateMentorTaskRequest(
        Subject subject,                    // 과목 1개
        Integer weekNumber,                 // 주차 (숫자, 표시용)
        LocalDate startDate,                // 시작일
        LocalDate endDate,                  // 종료일
        List<DayOfWeekEnum> daysOfWeek,     // 요일 복수선택
        String title,                       // 제목
        Long weaknessId,                    // nullable (보완점 선택 시)
        List<DayContentMapping> dayContents // 요일별 학습지 매핑
) {
    public record DayContentMapping(
            DayOfWeekEnum day,
            Long contentId
    ) {}
}
