package com.blaybus.blaybusbe.domain.submission.entity;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "task_submissions")
public class TaskSubmission extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentee_comment", length = 300)
    private String menteeComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionImage> images = new ArrayList<>();

    @Builder
    public TaskSubmission(String menteeComment, Task task) {
        this.menteeComment = menteeComment;
        this.task = task;
    }

    public void addImage(SubmissionImage image) {
        images.add(image);
        image.setSubmission(this);
    }
}
