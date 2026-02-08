package com.blaybus.blaybusbe.domain.task.controller;

import com.blaybus.blaybusbe.domain.task.controller.api.TaskApi;
import com.blaybus.blaybusbe.domain.task.dto.request.CreateMenteeTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.CreateMentorTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.UpdateTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.response.RecurringTaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskLogResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerStopResponse;

import java.time.LocalDate;
import java.util.List;
import com.blaybus.blaybusbe.domain.task.service.TaskService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    @PostMapping("/tasks/{menteeId}")
    public ResponseEntity<RecurringTaskResponse> createMentorTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @RequestBody CreateMentorTaskRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createMentorTask(user.getId(), menteeId, request));
    }

    @Override
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createMenteeTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateMenteeTaskRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createMenteeTask(user.getId(), request));
    }

    @Override
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @Override
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(user.getId(), user.getRole(), taskId, request));
    }

    @Override
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        taskService.deleteTask(user.getId(), user.getRole(), taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/mentor/tasks/{taskId}/confirm")
    public ResponseEntity<TaskResponse> confirmTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.confirmTask(user.getId(), taskId));
    }

    @Override
    @PatchMapping("/tasks/{taskId}/timer/start")
    public ResponseEntity<TimerResponse> startTimer(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.startTimer(user.getId(), taskId));
    }

    @Override
    @PatchMapping("/tasks/{taskId}/timer/stop")
    public ResponseEntity<TimerStopResponse> stopTimer(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.stopTimer(user.getId(), taskId));
    }

    @Override
    @GetMapping("/tasks/{taskId}/accumulated-study-time")
    public ResponseEntity<TaskResponse> getAccumulatedStudyTimeForTask(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(taskService.getAccumulatedStudyTimeForTask(taskId));
    }

    @Override
    @DeleteMapping("/mentor/recurring-tasks/{recurringGroupId}")
    public ResponseEntity<Void> deleteRecurringTasks(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String recurringGroupId
    ) {
        taskService.deleteRecurringTasks(user.getId(), recurringGroupId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/mentor/tasks/list/{menteeId}")
    public ResponseEntity<List<TaskResponse>> getTaskListByMentor(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(taskService.getTaskListForMentor(user.getId(), menteeId, date));
    }

    @Override
    @GetMapping("/mentee/tasks/list")
    public ResponseEntity<List<TaskResponse>> getTaskListByMentee(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(taskService.getTaskListForMentee(user.getId(), date));
    }
}
