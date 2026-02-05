package com.blaybus.blaybusbe.domain.mentoring.repository;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeInfoRepository extends JpaRepository<MenteeInfo, Long> {

    Optional<MenteeInfo> findByMenteeId(Long menteeId);

    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);
}
