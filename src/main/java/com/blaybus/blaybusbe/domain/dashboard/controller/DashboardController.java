package com.blaybus.blaybusbe.domain.dashboard.controller;

import com.blaybus.blaybusbe.domain.dashboard.controller.api.DashboardApi;
import com.blaybus.blaybusbe.domain.dashboard.dto.response.MenteeDashboardResponse;
import com.blaybus.blaybusbe.domain.dashboard.service.DashboardService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardService dashboardService;

    @GetMapping("/mentor/{menteeId}")
    public ResponseEntity<MenteeDashboardResponse> getMenteeDashboard(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @RequestParam(defaultValue = "WEEK") String type
    ) {

        return ResponseEntity.ok(dashboardService.getMenteeDashboard(menteeId, type));
    }

    @GetMapping("/mentee/me")
    public ResponseEntity<MenteeDashboardResponse> getMyDashboard(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "WEEK") String type
    ) {
        return ResponseEntity.ok(dashboardService.getMenteeDashboard(user.getId(), type));
    }
}