package com.blaybus.blaybusbe.domain.weeklyReport.repository;

import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
}
