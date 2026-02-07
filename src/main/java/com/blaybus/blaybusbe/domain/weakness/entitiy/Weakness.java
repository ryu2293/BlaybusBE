package com.blaybus.blaybusbe.domain.weakness.entitiy;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import com.blaybus.blaybusbe.domain.weakness.enums.Subject;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Weakness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 보완점

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", nullable = false)
    private MenteeInfo menteeInfo; // 멘티 정보 식별자 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private StudyContents studyContent;

    @Builder
    public Weakness(String title, Subject subject, MenteeInfo menteeInfo, StudyContents studyContent) {
        this.title = title;
        this.subject = subject;
        this.menteeInfo = menteeInfo;
        this.studyContent = studyContent;
    }
}
