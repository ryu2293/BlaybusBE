package com.blaybus.blaybusbe.domain.submission.repository;

import com.blaybus.blaybusbe.domain.submission.entity.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    Optional<TaskSubmission> findByTaskId(Long taskId);

    boolean existsByTaskId(Long taskId);
}
