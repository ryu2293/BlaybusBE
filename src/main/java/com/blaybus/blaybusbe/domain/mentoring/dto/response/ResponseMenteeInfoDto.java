package com.blaybus.blaybusbe.domain.mentoring.dto.response;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;

public record ResponseMenteeInfoDto(
        Long menteeId,
        String name,
        String profileImgUrl,
        String schoolName,
        Integer koreanGrade,
        Integer mathGrade,
        Integer englishGrade
) {

    public static ResponseMenteeInfoDto from(MenteeInfo menteeInfo) {
        return new ResponseMenteeInfoDto(
                menteeInfo.getMentee().getId(),
                menteeInfo.getMentee().getName(),
                menteeInfo.getMentee().getProfileImgUrl(),
                menteeInfo.getSchoolName(),
                menteeInfo.getKoreanGrade(),
                menteeInfo.getMathGrade(),
                menteeInfo.getEnglishGrade()
        );
    }
}