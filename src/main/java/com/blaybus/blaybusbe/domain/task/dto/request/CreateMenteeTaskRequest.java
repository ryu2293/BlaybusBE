package com.blaybus.blaybusbe.domain.task.dto.request;

import com.blaybus.blaybusbe.domain.task.enums.Subject;

import java.time.LocalDate;

public record CreateMenteeTaskRequest(
        LocalDate date,
        String title,
        Subject subject,
        String goal,
        String description
) {
}
