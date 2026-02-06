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

    /**
     * 줌 피드백 수정하는 함수
     *
     * @param title 제목
     * @param memo 메모
     * @param koreanFeedback 국어 피드백
     * @param mathFeedback 수학 피드백
     * @param englishFeedback 영어 피드백
     * @param operateFeedback 운영 피드백
     * @param meetingDate 미팅 날짜
     */
    public void update(String title, String memo, String koreanFeedback, String mathFeedback,
                       String englishFeedback, String operateFeedback, LocalDate meetingDate) {
        this.title = title;
        this.memo = memo;
        this.koreanFeedback = koreanFeedback;
        this.mathFeedback = mathFeedback;
        this.englishFeedback = englishFeedback;
        this.operateFeedback = operateFeedback;
        this.meetingDate = meetingDate;
    }
}