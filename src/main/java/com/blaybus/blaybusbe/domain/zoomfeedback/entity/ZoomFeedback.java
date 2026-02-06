package com.blaybus.blaybusbe.domain.zoomfeedback.entity;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "zoom_feedbacks")
public class ZoomFeedback extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String memo;

    @Column(name = "korean_feedback", columnDefinition = "TEXT")
    private String koreanFeedback;

    @Column(name = "math_feedback", columnDefinition = "TEXT")
    private String mathFeedback;

    @Column(name = "english_feedback", columnDefinition = "TEXT")
    private String englishFeedback;

    @Column(name = "operate_feedback", columnDefinition = "TEXT")
    private String operateFeedback;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", nullable = false)
    private MenteeInfo menteeInfo;

    @Builder
    public ZoomFeedback(String title, String memo, String koreanFeedback, String mathFeedback,
                        String englishFeedback, String operateFeedback, LocalDate meetingDate,
                        MenteeInfo menteeInfo) {
        this.title = title;
        this.memo = memo;
        this.koreanFeedback = koreanFeedback;
        this.mathFeedback = mathFeedback;
        this.englishFeedback = englishFeedback;
        this.operateFeedback = operateFeedback;
        this.meetingDate = meetingDate;
        this.menteeInfo = menteeInfo;
    }
}