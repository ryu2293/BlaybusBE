package com.blaybus.blaybusbe.domain.plan.controller;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.plan.controller.api.PlanApi;
import com.blaybus.blaybusbe.domain.plan.dto.request.CreatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.PlanFeedbackRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.UpdatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.response.CalendarDayResponse;
import com.blaybus.blaybusbe.domain.plan.dto.response.PlanFeedbackResponse;
import com.blaybus.blaybusbe.domain.plan.dto.response.PlanResponse;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import com.blaybus.blaybusbe.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController implements PlanApi {

    private final PlanService planService;
    private final UserRepository userRepository;
    private final MenteeInfoRepository menteeInfoRepository;

    @Override
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreatePlanRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.createPlan(user.getId(), user.getRole(), request));
    }

    @Override
    @GetMapping
    public ResponseEntity<PlanResponse> getPlan(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam(required = false) Long menteeId
    ) {
        LocalDate date = LocalDate.of(year, month, day);

        if (menteeId != null) {
            // 멘토-멘티 매핑 확인
            if (!menteeInfoRepository.existsByMentorIdAndMenteeId(user.getId(), menteeId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
            return ResponseEntity.ok(planService.getMenteePlanByDate(menteeId, date));
        }

        // 본인 플래너 조회
        return ResponseEntity.ok(planService.getPlanByDate(user.getId(), date));
    }

    @Override
    @GetMapping("/calendar")
    public ResponseEntity<Page<CalendarDayResponse>> getCalendar(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long menteeId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Subject subject,
            @RequestParam(required = false) Boolean incompleteOnly,
            @PageableDefault(size = 31, sort = "planDate") Pageable pageable
    ) {
        if (menteeId != null && !menteeInfoRepository.existsByMentorIdAndMenteeId(user.getId(), menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        Long targetMenteeId = menteeId != null ? menteeId : user.getId();
        return ResponseEntity.ok(planService.getCalendar(targetMenteeId, year, month, subject, incompleteOnly, pageable));
    }

    @Override
    @GetMapping("/calendar/weekly")
    public ResponseEntity<Page<PlanResponse>> getWeeklyCalendar(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long menteeId,
            @RequestParam String date,
            @PageableDefault(size = 7, sort = "planDate") Pageable pageable
    ) {
        if (menteeId != null && !menteeInfoRepository.existsByMentorIdAndMenteeId(user.getId(), menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        Long targetMenteeId = menteeId != null ? menteeId : user.getId();
        LocalDate targetDate = LocalDate.parse(date);
        return ResponseEntity.ok(planService.getWeeklyCalendar(targetMenteeId, targetDate, pageable));
    }

    @Override
    @PutMapping("/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody UpdatePlanRequest request
    ) {
        return ResponseEntity.ok(planService.updatePlan(user.getId(), planId, request));
    }

    @Override
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    ) {
        planService.deletePlan(user.getId(), planId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{planId}/feedback")
    public ResponseEntity<PlanFeedbackResponse> createFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody PlanFeedbackRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.createFeedback(user.getId(), user.getRole(), planId, request));
    }

    @Override
    @GetMapping("/{planId}/feedback")
    public ResponseEntity<PlanFeedbackResponse> getFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    ) {
        // 피드백 조회 시 멘토 이름을 전달하기 위해 사용자 정보 조회
        User currentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(planService.getFeedback(planId, currentUser.getName()));
    }

    @Override
    @PutMapping("/{planId}/feedback")
    public ResponseEntity<PlanFeedbackResponse> updateFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody PlanFeedbackRequest request
    ) {
        return ResponseEntity.ok(planService.updateFeedback(user.getId(), user.getRole(), planId, request));
    }

    @Override
    @DeleteMapping("/{planId}/feedback")
    public ResponseEntity<Void> deleteFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    ) {
        planService.deleteFeedback(user.getId(), user.getRole(), planId);
        return ResponseEntity.noContent().build();
    }
}
