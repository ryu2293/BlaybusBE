package com.blaybus.blaybusbe.domain.comment.repository;

import com.blaybus.blaybusbe.domain.comment.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a JOIN FETCH a.user WHERE a.feedback.id = :feedbackId ORDER BY a.createdAt ASC")
    List<Answer> findByFeedbackIdOrderByCreatedAtAsc(@Param("feedbackId") Long feedbackId);

    int countByFeedbackId(Long feedbackId);

    @Query("SELECT COUNT(a) FROM Answer a " +
            "JOIN a.feedback f " +
            "JOIN f.task t " +
            "WHERE t.mentee.id = :menteeId " +
            "AND a.user.role = 'MENTEE' " +
            "AND a.isMentorRead = false")
    long countUncheckedQuestionsByMentee(@Param("menteeId") Long menteeId);
}
