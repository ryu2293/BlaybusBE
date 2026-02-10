package com.blaybus.blaybusbe.domain.notification.dto.response;

import com.blaybus.blaybusbe.domain.notification.entity.Notification;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // 프론트 라우팅용 필드
    private String targetType;
    private Long targetId;
    private Long feedbackId;
    private Long taskId;
    private Long menteeId;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .targetType(notification.getType().name())
                .targetId(notification.getTargetId())
                .feedbackId(notification.getFeedbackId())
                .taskId(notification.getTaskId())
                .menteeId(notification.getMenteeId())
                .build();
    }
}
