package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;

public record UpdateTaskRequest(
        String title,
        Subject subject,
        String goal,
        String description,
        TaskStatus status,
        Boolean isMandatory
) {
}
