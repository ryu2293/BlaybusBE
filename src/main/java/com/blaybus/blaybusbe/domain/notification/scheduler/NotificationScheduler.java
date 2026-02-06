package com.blaybus.blaybusbe.domain.notification.scheduler;

import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.notification.service.NotificationService;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void sendDailyReminder() {
        LocalDate today = LocalDate.now();
        List<Task> incompleteTasks = taskRepository.findByTaskDateAndStatusNot(today, TaskStatus.DONE);

        Map<Long, List<Task>> tasksByMentee = incompleteTasks.stream()
                .collect(Collectors.groupingBy(task -> task.getMentee().getId()));

        for (Map.Entry<Long, List<Task>> entry : tasksByMentee.entrySet()) {
            Long menteeId = entry.getKey();
            int count = entry.getValue().size();

            try {
                String message = String.format("오늘 완료하지 않은 과제가 %d개 있습니다.", count);
                notificationService.send(NotificationType.REMINDER, menteeId, message);
            } catch (Exception e) {
                log.error("리마인더 알림 전송 실패: menteeId={}", menteeId, e);
            }
        }

        log.info("일일 리마인더 알림 전송 완료: {}명", tasksByMentee.size());
    }
}
