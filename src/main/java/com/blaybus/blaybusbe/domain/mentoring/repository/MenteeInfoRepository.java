package com.blaybus.blaybusbe.domain.mentoring.repository;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenteeInfoRepository extends JpaRepository<MenteeInfo, Long> {

    Optional<MenteeInfo> findByMenteeId(Long menteeId);

    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Query("SELECT mi FROM MenteeInfo mi JOIN FETCH mi.mentor WHERE mi.mentee.id = :menteeId")
    Optional<MenteeInfo> findByMenteeIdWithMentor(@Param("menteeId") Long menteeId);

    Optional<MenteeInfo> findByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    /**
     *
     * @param mentorId 멘토 id
     * @param pageable 페이지네이션
     */
    @Query(
            "SELECT m FROM MenteeInfo m JOIN FETCH m.mentee WHERE m.mentor.id = :mentorId"
    )
    Page<MenteeInfo> searchMyMentees(
            @Param("mentorId") Long mentorId,
            Pageable pageable
    );
}
