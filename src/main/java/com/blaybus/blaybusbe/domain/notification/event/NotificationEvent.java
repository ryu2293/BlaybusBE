package com.blaybus.blaybusbe.domain.notification.event;

import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;

public record NotificationEvent(
        NotificationType type,
        Long recipientUserId,
        String message,
        Long targetId,
        Long feedbackId,
        Long taskId,
        Long menteeId
) {
    // 기존 호환용 생성자 (targetId, feedbackId, taskId, menteeId 없는 경우)
    public NotificationEvent(NotificationType type, Long recipientUserId, String message) {
        this(type, recipientUserId, message, null, null, null, null);
    }
}
