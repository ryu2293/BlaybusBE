package com.blaybus.blaybusbe.domain.task.controller.api;

import com.blaybus.blaybusbe.domain.task.dto.request.CreateMenteeTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.CreateMentorTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.UpdateTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.response.RecurringTaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskLogResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerStopResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "과제/할일 API", description = "멘토 과제 출제, 멘티 할 일 추가, 타이머 API")
public interface TaskApi {

    @Operation(summary = "멘토 과제 출제",
            description = "멘토가 멘티에게 과제를 출제합니다. date만 전달하면 단일 과제, startDate/endDate/daysOfWeek를 전달하면 반복 과제를 일괄 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "과제 출제 성공",
                    content = @Content(schema = @Schema(implementation = RecurringTaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "멘토-멘티 매핑 없음", content = @Content)
    })
    ResponseEntity<RecurringTaskResponse> createMentorTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "멘티 ID") @PathVariable Long menteeId,
            @RequestBody CreateMentorTaskRequest request
    );

    @Operation(summary = "멘티 할 일 추가", description = "멘티가 본인 캘린더에 할 일을 추가합니다. (is_mandatory=false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "할 일 추가 성공",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    ResponseEntity<TaskResponse> createMenteeTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateMenteeTaskRequest request
    );

    @Operation(summary = "과제 상세 조회", description = "과제/할 일 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "과제 없음", content = @Content)
    })
    ResponseEntity<TaskResponse> getTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "과제 수정", description = "과제/할 일을 수정합니다. 고정 과제는 멘토만 수정 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "과제 없음", content = @Content)
    })
    ResponseEntity<TaskResponse> updateTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request
    );

    @Operation(summary = "과제 삭제", description = "과제/할 일을 삭제합니다. 고정 과제는 멘토만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "과제 없음", content = @Content)
    })
    ResponseEntity<Void> deleteTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "멘토 확인 체크 토글", description = "멘토가 과제 확인 여부를 토글합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토글 성공",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "과제 없음", content = @Content)
    })
    ResponseEntity<TaskResponse> confirmTask(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "타이머 시작", description = "과제 타이머를 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타이머 시작 성공",
                    content = @Content(schema = @Schema(implementation = TimerResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 실행 중", content = @Content)
    })
    ResponseEntity<TimerResponse> startTimer(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "타이머 종료", description = "과제 타이머를 종료하고 시간을 누적합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타이머 종료 성공",
                    content = @Content(schema = @Schema(implementation = TimerStopResponse.class))),
            @ApiResponse(responseCode = "409", description = "실행 중이 아님", content = @Content)
    })
    ResponseEntity<TimerStopResponse> stopTimer(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "타이머 기록 조회", description = "과제의 타이머 세션 기록 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "과제 없음", content = @Content)
    })
    ResponseEntity<List<TaskLogResponse>> getTaskLogs(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "과제 ID") @PathVariable Long taskId
    );

    @Operation(summary = "반복 과제 그룹 삭제", description = "같은 반복 그룹의 과제를 일괄 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "반복 그룹 없음", content = @Content)
    })
    ResponseEntity<Void> deleteRecurringTasks(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "반복 그룹 ID") @PathVariable String recurringGroupId
    );

    @Operation(summary = "[멘토] 멘티의 날짜별 과제 목록 조회", description = "멘토가 담당 멘티의 특정 날짜 과제 목록을 조회합니다.")
    ResponseEntity<List<TaskResponse>> getTaskListByMentor(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "멘티 id", required = true , example = "1")
            @PathVariable Long menteeId,

            @Parameter(description = "조회 날짜", required = true )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @Operation(summary = "[멘티] 본인의 날짜별 과제 목록 조회", description = "멘티 본인의 특정 날짜 과제 목록을 조회합니다.")
    ResponseEntity<List<TaskResponse>> getTaskListByMentee(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "조회 날짜", required = true )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );
}
