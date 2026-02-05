package com.blaybus.blaybusbe.domain.mentoring.dto.response;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;

public record ResponseMenteeInfoDto(
        Long menteeId,
        String name,
        String profileImgUrl,

        // TODO: 현재는 0이나 null 반환하지만 추후 과제 테이블 생성 시 수정
        Integer koreanProgress,
        Integer mathProgress,
        Integer englishProgress
) {

    public static ResponseMenteeInfoDto from(MenteeInfo menteeInfo) {
        return new ResponseMenteeInfoDto(
                menteeInfo.getMentee().getId(),
                menteeInfo.getMentee().getName(),
                menteeInfo.getMentee().getProfileImgUrl(),
                0,
                0,
                0
        );
    }
}