package com.blaybus.blaybusbe.domain.mentoring.dto.response;

import com.blaybus.blaybusbe.domain.user.entity.User;

public record ResponseMyMentorDto(
        String name,
        String nickName,
        String profileUrl
) {
    public static ResponseMyMentorDto from(User mentor) {
        return new ResponseMyMentorDto(
                mentor.getName(),
                mentor.getNickname(),
                mentor.getProfileImgUrl()
        );
    }
}
