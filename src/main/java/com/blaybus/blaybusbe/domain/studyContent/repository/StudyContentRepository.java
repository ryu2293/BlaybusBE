package com.blaybus.blaybusbe.domain.studyContent.repository;

import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyContentRepository extends JpaRepository<StudyContents, Long> {
}
