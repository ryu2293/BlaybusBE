package com.blaybus.blaybusbe.domain.weeklyReport.repository;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    // 특정 멘티의 연도/월별 주간 보고서를 조회
    Page<WeeklyReport> findAllByMenteeInfoAndReportYearAndReportMonth(
            MenteeInfo menteeInfo, Integer reportYear, Integer reportMonth, Pageable pageable);
}
