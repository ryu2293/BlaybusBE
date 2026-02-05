package com.blaybus.blaybusbe.domain.weeklyReport.entity;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_reports")
public class WeeklyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer reportYear;
    @Column(nullable = false)
    private Integer reportMonth;
    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String overallFeedback; // 총평

    private String strengths; // 잘한 점
    private String weaknesses; // 보완할 점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id")
    private MenteeInfo menteeInfo;

    @Builder
    public WeeklyReport(Integer year, Integer month, Integer weekNumber, LocalDate startDate, LocalDate endDate,
                        String overallFeedback, String strengths, String weaknesses, MenteeInfo menteeInfo) {
        this.reportYear = year;
        this.reportMonth = month;
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.overallFeedback = overallFeedback;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.menteeInfo = menteeInfo;
    }
}
