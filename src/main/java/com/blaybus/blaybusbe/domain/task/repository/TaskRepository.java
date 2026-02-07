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

    // 과제 제출 카운트
    long countByMenteeIdAndStatusAndIsMentorChecked(Long menteeId, TaskStatus status, boolean isMentorChecked);

    // 남은 과제 카운트
    long countByMenteeIdAndIsMandatoryAndStatusNot(Long menteeId, boolean isMandatory, TaskStatus status);

    // 과목별 필수 과제 총합 및 확인된 수 (진행률 계산용)
    long countByMenteeIdAndSubjectAndIsMandatory(Long menteeId, Subject subject, boolean isMandatory);
    long countByMenteeIdAndSubjectAndIsMandatoryAndIsMentorChecked(Long menteeId, Subject subject, boolean isMandatory, boolean isMentorChecked);
}
