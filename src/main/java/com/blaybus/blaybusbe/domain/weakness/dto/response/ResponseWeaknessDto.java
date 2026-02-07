package com.blaybus.blaybusbe.domain.weakness.dto.response;

import com.blaybus.blaybusbe.domain.weakness.entitiy.Weakness;
import com.blaybus.blaybusbe.domain.weakness.enums.Subject;

public record ResponseWeaknessDto(
        Long id,
        String title,
        Subject subject,
        Long contentId,
        String contentTitle,    // 학습 자료 제목
        String contentUrl       // 학습 자료 PDF URL
) {
    public static ResponseWeaknessDto from(Weakness entity) {
        return new ResponseWeaknessDto(
                entity.getId(),
                entity.getTitle(),
                entity.getSubject(),
                entity.getStudyContent() != null ? entity.getStudyContent().getId() : null,
                entity.getStudyContent() != null ? entity.getStudyContent().getTitle() : null,
                entity.getStudyContent() != null ? entity.getStudyContent().getContentUrl() : null
        );
    }
}