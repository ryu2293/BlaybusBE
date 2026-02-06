package com.blaybus.blaybusbe.domain.feedback.entity;

import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "task_feedbacks")
public class TaskFeedback extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String content;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "x_pos", nullable = false)
    private Float xPos;

    @Column(name = "y_pos", nullable = false)
    private Float yPos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private SubmissionImage image;

    @Builder
    public TaskFeedback(String content, String imageUrl, Float xPos, Float yPos,
                        Task task, User mentor, SubmissionImage image) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.xPos = xPos;
        this.yPos = yPos;
        this.task = task;
        this.mentor = mentor;
        this.image = image;
    }

    public void update(String content, String imageUrl, Float xPos, Float yPos) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
