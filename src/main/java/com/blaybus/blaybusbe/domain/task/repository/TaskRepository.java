package com.blaybus.blaybusbe.domain.task.repository;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByDailyPlanId(Long dailyPlanId);

    List<Task> findByMenteeIdAndTaskDate(Long menteeId, LocalDate taskDate);

    List<Task> findByRecurringGroupId(String recurringGroupId);

    List<Task> findByTaskDateAndStatusNot(LocalDate taskDate, TaskStatus status);

}
