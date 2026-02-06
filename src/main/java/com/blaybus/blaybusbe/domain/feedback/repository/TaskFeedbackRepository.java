package com.blaybus.blaybusbe.domain.feedback.repository;

import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskFeedbackRepository extends JpaRepository<TaskFeedback, Long> {

    @Query("SELECT f FROM TaskFeedback f JOIN FETCH f.mentor WHERE f.image.id = :imageId ORDER BY f.createdAt DESC")
    List<TaskFeedback> findByImageIdWithMentor(@Param("imageId") Long imageId);

    @Query("SELECT COUNT(f) FROM TaskFeedback f WHERE f.image.id = :imageId")
    int countByImageId(@Param("imageId") Long imageId);
}
