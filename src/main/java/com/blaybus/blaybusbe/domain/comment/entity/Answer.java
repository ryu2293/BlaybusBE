package com.blaybus.blaybusbe.domain.comment.entity;

import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "answer")
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String comment;

    @Column(name = "is_mentor_read", nullable = false)
    private Boolean isMentorRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private TaskFeedback feedback;

    @Builder
    public Answer(String comment, User user, TaskFeedback feedback) {
        this.comment = comment;
        this.isMentorRead = false;
        this.user = user;
        this.feedback = feedback;
    }

    public void update(String comment) {
        this.comment = comment;
    }

    // 읽음 상태 업데이트 메서드
    public void markAsRead() {
        this.isMentorRead = true;
    }
}
