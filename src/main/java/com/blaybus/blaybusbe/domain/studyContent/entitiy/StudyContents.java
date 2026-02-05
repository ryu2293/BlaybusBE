package com.blaybus.blaybusbe.domain.studyContent.entitiy;

import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "study_contents")
public class StudyContents extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Column(columnDefinition = "TEXT")
    private String contentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @Builder
    public StudyContents(String title, Subject subject, String contentUrl, User mentor) {
        this.title = title;
        this.subject = subject;
        this.contentUrl = contentUrl;
        this.mentor = mentor;
    }

    /**
     * 비즈니스 로직: 학습 자료 수정
     */
    public void updateContent(String title, Subject subject, String contentUrl) {
        if (title != null) this.title = title;
        if (subject != null) this.subject = subject;
        if (contentUrl != null) this.contentUrl = contentUrl;
    }
}
