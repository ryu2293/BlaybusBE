package com.blaybus.blaybusbe.domain.plan.entity;

import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "daily_planners", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mentee_id", "plan_date"})
})
public class DailyPlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "total_study_time", nullable = false)
    private Long totalStudyTime = 0L;

    @Column(name = "daily_memo", columnDefinition = "TEXT")
    private String dailyMemo;

    @Column(name = "mentor_feedback", columnDefinition = "TEXT")
    private String mentorFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Builder
    public DailyPlan(LocalDate planDate, String dailyMemo, User mentee) {
        this.planDate = planDate;
        this.dailyMemo = dailyMemo;
        this.mentee = mentee;
        this.totalStudyTime = 0L;
    }
}
