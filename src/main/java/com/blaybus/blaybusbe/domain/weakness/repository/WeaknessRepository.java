package com.blaybus.blaybusbe.domain.weakness.repository;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.weakness.entitiy.Weakness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeaknessRepository extends JpaRepository<Weakness, Long> {

    // 멘티 정보별 약점 페이징 조회
    @Query(value = "SELECT w FROM Weakness w LEFT JOIN FETCH w.studyContent WHERE w.menteeInfo = :menteeInfo",
            countQuery = "SELECT count(w) FROM Weakness w WHERE w.menteeInfo = :menteeInfo")
    Page<Weakness> findAllByMenteeInfo(@Param("menteeInfo") MenteeInfo menteeInfo, Pageable pageable);
}
