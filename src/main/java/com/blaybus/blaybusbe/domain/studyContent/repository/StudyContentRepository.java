package com.blaybus.blaybusbe.domain.studyContent.repository;

import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyContentRepository extends JpaRepository<StudyContents, Long> {

    @Query("SELECT s FROM StudyContents s " +
            "WHERE s.mentor.id = :mentorId OR s.mentor IS NULL")
    Page<StudyContents> findAllByMentorIdOrAdmin(@Param("mentorId") Long mentorId, Pageable pageable);
}
