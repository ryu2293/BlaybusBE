package com.blaybus.blaybusbe.domain.task.entity;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.domain.task.enums.TimerStatus;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tasks")
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "actual_study_time", nullable = false)
    private Integer actualStudyTime = 0;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @Column(name = "is_mentor_checked", nullable = false)
    private Boolean isMentorChecked = false;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = false;

    @Column(name = "recurring_group_id")
    private String recurringGroupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "timer_status", nullable = false)
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    @Column(name = "timer_started_at")
    private LocalDateTime timerStartedAt;

    @Column(name = "weakness_id")
    private Long weaknessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_planner_id", nullable = false)
    private DailyPlan dailyPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Builder
    public Task(Subject subject, String title, LocalDate taskDate,
                Boolean isMandatory, String recurringGroupId, Long weaknessId,
                DailyPlan dailyPlan, User mentee) {
        this.subject = subject;
        this.title = title;
        this.taskDate = taskDate;
        this.isMandatory = isMandatory != null ? isMandatory : false;
        this.recurringGroupId = recurringGroupId;
        this.weaknessId = weaknessId;
        this.dailyPlan = dailyPlan;
        this.mentee = mentee;
        this.status = TaskStatus.TODO;
        this.actualStudyTime = 0;
        this.isMentorChecked = false;
        this.timerStatus = TimerStatus.STOPPED;
    }
}
