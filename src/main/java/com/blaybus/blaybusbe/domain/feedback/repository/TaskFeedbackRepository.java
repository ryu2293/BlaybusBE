package com.blaybus.blaybusbe.domain.feedback.repository;

import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskFeedbackRepository extends JpaRepository<TaskFeedback, Long> {

    @Query("SELECT f FROM TaskFeedback f JOIN FETCH f.mentor WHERE f.image.id = :imageId ORDER BY f.createdAt DESC")
    List<TaskFeedback> findByImageIdWithMentor(@Param("imageId") Long imageId);

    @Query("SELECT COUNT(f) FROM TaskFeedback f WHERE f.image.id = :imageId")
    int countByImageId(@Param("imageId") Long imageId);

    // 어제자 피드백 목록 조회 (피드백 생성일 기준, 페이징)
    @Query(value = "SELECT f FROM TaskFeedback f JOIN FETCH f.mentor JOIN FETCH f.task " +
            "WHERE f.task.mentee.id = :menteeId " +
            "AND CAST(f.createdAt AS localdate) = :yesterday " +
            "ORDER BY f.createdAt DESC",
            countQuery = "SELECT COUNT(f) FROM TaskFeedback f " +
                    "WHERE f.task.mentee.id = :menteeId " +
                    "AND CAST(f.createdAt AS localdate) = :yesterday")
    Page<TaskFeedback> findYesterdayFeedbacks(@Param("menteeId") Long menteeId,
                                              @Param("yesterday") LocalDate yesterday,
                                              Pageable pageable);

    // 이전 피드백 모아보기 (피드백 생성일 기준 필터링 + 페이징)
    @Query(value = "SELECT f FROM TaskFeedback f JOIN FETCH f.mentor JOIN FETCH f.task " +
            "WHERE f.task.mentee.id = :menteeId " +
            "AND (:subject IS NULL OR f.task.subject = :subject) " +
            "AND (:year IS NULL OR YEAR(f.createdAt) = :year) " +
            "AND (:month IS NULL OR MONTH(f.createdAt) = :month) " +
            "AND (:startDate IS NULL OR CAST(f.createdAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(f.createdAt AS localdate) <= :endDate) " +
            "ORDER BY f.createdAt DESC",
            countQuery = "SELECT COUNT(f) FROM TaskFeedback f " +
                    "WHERE f.task.mentee.id = :menteeId " +
                    "AND (:subject IS NULL OR f.task.subject = :subject) " +
                    "AND (:year IS NULL OR YEAR(f.createdAt) = :year) " +
                    "AND (:month IS NULL OR MONTH(f.createdAt) = :month) " +
                    "AND (:startDate IS NULL OR CAST(f.createdAt AS localdate) >= :startDate) " +
                    "AND (:endDate IS NULL OR CAST(f.createdAt AS localdate) <= :endDate)")
    Page<TaskFeedback> findFeedbacksWithFilters(@Param("menteeId") Long menteeId,
                                                @Param("subject") Subject subject,
                                                @Param("year") Integer year,
                                                @Param("month") Integer month,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                Pageable pageable);
}
