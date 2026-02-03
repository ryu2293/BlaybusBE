package com.blaybus.blaybusbe.domain.user.dto.response;

import com.blaybus.blaybusbe.domain.user.entity.User;
import lombok.Builder;

@Builder
public record ResponseUserDto(
        Long userId,
        String username,
        String name,
        String nickName,
        String role,
        String profileUrl
) {

    public static ResponseUserDto from(User user) {

        return ResponseUserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .nickName(user.getNickname())
                .role(user.getRole().name())
                .profileUrl(user.getProfileImgUrl())
                .build();
    }
}
