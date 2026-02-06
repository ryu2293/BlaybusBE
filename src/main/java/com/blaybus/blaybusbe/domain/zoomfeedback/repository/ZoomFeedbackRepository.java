package com.blaybus.blaybusbe.domain.zoomfeedback.repository;

import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZoomFeedbackRepository extends JpaRepository<ZoomFeedback, Long> {

    // 줌 미팅 피드백 목록 조회
    @Query("SELECT z FROM ZoomFeedback z " +
            "JOIN FETCH z.menteeInfo mi " +
            "JOIN FETCH mi.mentee " +
            "WHERE mi.mentor.id = :mentorId AND mi.mentee.id = :menteeId")
    Page<ZoomFeedback> findByMentorIdAndMenteeId(
            @Param("mentorId") Long mentorId,
            @Param("menteeId") Long menteeId,
            Pageable pageable);
}
