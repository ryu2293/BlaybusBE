package com.blaybus.blaybusbe.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 서버 관련 오류
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다."),

    // 존재하지 않는 사용자
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다.");

    private final int status;
    private final String message;
}