package com.blaybus.blaybusbe.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MENTOR,
    MENTEE,
    WITH_DRAW // 탈퇴
}
