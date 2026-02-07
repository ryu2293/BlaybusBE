package com.blaybus.blaybusbe.domain.weakness.repository;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.weakness.entitiy.Weakness;
import com.blaybus.blaybusbe.domain.weakness.enums.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeaknessRepository extends JpaRepository<Weakness, Long> {

    // 과목 필터링을 포함한 약점 페이징 조회
    @Query(value = "SELECT w FROM Weakness w " +
            "LEFT JOIN FETCH w.studyContent " +
            "WHERE w.menteeInfo = :menteeInfo " +
            "AND (:subject IS NULL OR w.subject = :subject)",
            countQuery = "SELECT count(w) FROM Weakness w " +
                    "WHERE w.menteeInfo = :menteeInfo " +
                    "AND (:subject IS NULL OR w.subject = :subject)")
    Page<Weakness> findAllByMenteeInfoAndSubject(
            @Param("menteeInfo") MenteeInfo menteeInfo,
            @Param("subject") Subject subject,
            Pageable pageable
    );
}
