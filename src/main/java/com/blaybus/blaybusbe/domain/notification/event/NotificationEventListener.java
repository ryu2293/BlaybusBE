package com.blaybus.blaybusbe.domain.notification.event;

import com.blaybus.blaybusbe.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 수신: type={}, userId={}, message={}",
                event.type(), event.recipientUserId(), event.message());
        try {
            notificationService.send(event.type(), event.recipientUserId(), event.message());
            log.info("알림 저장 완료: type={}, userId={}", event.type(), event.recipientUserId());
        } catch (Exception e) {
            log.error("알림 처리 실패: type={}, userId={}, message={}",
                    event.type(), event.recipientUserId(), event.message(), e);
        }
    }
}
