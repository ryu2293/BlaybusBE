package com.blaybus.blaybusbe.domain.plan.repository;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    Optional<DailyPlan> findByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);

    Page<DailyPlan> findByMenteeIdAndPlanDateBetween(Long menteeId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);
}
