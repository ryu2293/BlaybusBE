package com.blaybus.blaybusbe.domain.plan.repository;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {

    Optional<DailyPlan> findByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);

    Page<DailyPlan> findByMenteeIdAndPlanDateBetween(Long menteeId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT DISTINCT dp FROM DailyPlan dp JOIN Task t ON t.dailyPlan = dp " +
           "WHERE dp.mentee.id = :menteeId " +
           "AND dp.planDate BETWEEN :startDate AND :endDate " +
           "AND (:subject IS NULL OR t.subject = :subject) " +
           "AND (:incompleteOnly = false OR t.status <> :doneStatus)")
    Page<DailyPlan> findByMenteeIdAndPlanDateBetweenWithFilters(
            @Param("menteeId") Long menteeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("subject") Subject subject,
            @Param("incompleteOnly") boolean incompleteOnly,
            @Param("doneStatus") TaskStatus doneStatus,
            Pageable pageable
    );

    boolean existsByMenteeIdAndPlanDate(Long menteeId, LocalDate planDate);

    // 오늘 날짜의 플래너가 있고, 아직 멘토 피드백이 비어있는지 확인
    boolean existsByMenteeIdAndPlanDateAndMentorFeedbackIsNull(Long menteeId, LocalDate planDate);
}
