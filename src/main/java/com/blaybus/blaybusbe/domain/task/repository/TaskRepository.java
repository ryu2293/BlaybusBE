package com.blaybus.blaybusbe.domain.task.repository;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByDailyPlanId(Long dailyPlanId);

    List<Task> findByMenteeIdAndTaskDate(Long menteeId, LocalDate taskDate);

    List<Task> findByRecurringGroupId(String recurringGroupId);

    List<Task> findByTaskDateAndStatusNot(LocalDate taskDate, TaskStatus status);

    // 기간별 과제 제출 카운트 (DONE & !isChecked)
    long countByMenteeIdAndStatusAndIsMentorCheckedAndTaskDateBetween(Long menteeId, TaskStatus status, boolean isMentorChecked, LocalDate start, LocalDate end);

    // 남은 과제 카운트
    long countByMenteeIdAndIsMandatoryAndStatusNot(Long menteeId, boolean isMandatory, TaskStatus status);

    // 기간별 과목 진행률 계산을 위한 쿼리
    long countByMenteeIdAndSubjectAndIsMandatoryAndTaskDateBetween(Long menteeId, Subject subject, boolean isMandatory, LocalDate start, LocalDate end);
    long countByMenteeIdAndSubjectAndIsMandatoryAndIsMentorCheckedAndTaskDateBetween(Long menteeId, Subject subject, boolean isMandatory, boolean isMentorChecked, LocalDate start, LocalDate end);
}
