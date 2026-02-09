package com.blaybus.blaybusbe.domain.mentoring.entity;

import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mentee_info")
public class MenteeInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_name", nullable = false, length = 30)
    private String schoolName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false, unique = true)
    private User mentee;

    @Builder
    public MenteeInfo(String schoolName, Integer koreanGrade, Integer mathGrade, Integer englishGrade, User mentor, User mentee) {
        this.schoolName = schoolName;
        this.mentor = mentor;
        this.mentee = mentee;
    }
}
