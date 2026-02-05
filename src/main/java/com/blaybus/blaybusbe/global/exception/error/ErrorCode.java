package com.blaybus.blaybusbe.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 서버 관련 오류
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다."),

    // 존재하지 않는 사용자
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),

    // s3 업로드
    S3_UPLOAD_ERROR(500, "파일 업로드에 실패하였습니다."),

    // 플래너 관련 오류
    PLAN_NOT_FOUND(404, "존재하지 않는 플래너입니다."),
    PLAN_DUPLICATE_DATE(409, "해당 날짜에 이미 플래너가 존재합니다."),
    PLAN_FEEDBACK_NOT_FOUND(404, "플래너 피드백이 존재하지 않습니다."),
    PLAN_FEEDBACK_ALREADY_EXISTS(409, "이미 피드백이 작성되어 있습니다."),

    // Task 관련 오류
    TASK_NOT_FOUND(404, "존재하지 않는 과제입니다."),
    TASK_NOT_MODIFIABLE(403, "해당 과제를 수정/삭제할 권한이 없습니다."),
    TIMER_ALREADY_RUNNING(409, "타이머가 이미 실행 중입니다."),
    TIMER_NOT_RUNNING(409, "타이머가 실행 중이 아닙니다."),
    RECURRING_GROUP_NOT_FOUND(404, "반복 과제 그룹을 찾을 수 없습니다."),
    MENTEE_INFO_NOT_FOUND(404, "멘토-멘티 매핑 정보를 찾을 수 없습니다."),

    // 학습자료 관련 오류
    CONTENT_NOT_FOUND(404, "해당 학습자료는 존재하지 않습니다."),
    FORBIDDEN_ACCESS(403, "해당 자료를 삭제할 권한이 없습니다."),

    // 권한 관련 오류
    UNAUTHORIZED_ACCESS(403, "접근 권한이 없습니다.");

    private final int status;
    private final String message;
}