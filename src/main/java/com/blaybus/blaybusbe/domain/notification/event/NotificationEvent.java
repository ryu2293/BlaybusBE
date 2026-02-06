package com.blaybus.blaybusbe.domain.notification.event;

import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;

public record NotificationEvent(
        NotificationType type,
        Long recipientUserId,
        String message
) {
}
