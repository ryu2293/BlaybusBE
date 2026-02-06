package com.blaybus.blaybusbe.domain.zoomfeedback.repository;

import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomFeedbackRepository extends JpaRepository<ZoomFeedback, Long> {
}
