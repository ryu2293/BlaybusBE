package com.blaybus.blaybusbe.domain.studyContent.dto.response;

import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;

public record ResponseContentDto(
        Long id,
        String title,
        Subject subject,
        String contentUrl,
        boolean isOfficial
) {
    public static ResponseContentDto from(StudyContents entity) {
        return new ResponseContentDto(
                entity.getId(),
                entity.getTitle(),
                entity.getSubject(),
                entity.getContentUrl(),
                entity.getMentor() == null // 멘토가 Null이면 설스터디에서 제공한 학습자료
        );
    }
}