package com.blaybus.blaybusbe.domain.notification.entity;

import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 프론트 라우팅용 필드
    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "mentee_id")
    private Long menteeId;

    @Builder
    public Notification(NotificationType type, String message, User user,
                        Long targetId, Long feedbackId, Long taskId, Long menteeId) {
        this.type = type;
        this.message = message;
        this.user = user;
        this.isRead = false;
        this.targetId = targetId;
        this.feedbackId = feedbackId;
        this.taskId = taskId;
        this.menteeId = menteeId;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
