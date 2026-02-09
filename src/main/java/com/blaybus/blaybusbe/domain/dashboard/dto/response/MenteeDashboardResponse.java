package com.blaybus.blaybusbe.domain.dashboard.dto.response;

public record MenteeDashboardResponse(
        Long menteeId,
        String name,
        String profileImgUrl,
        String schoolName,

        // 대시보드 수치 지표
        long submittedTasksCount,
        long remainingTasksCount,
        long feedbackQuestionsCount,
        long todayPlannerTodoCount,

        // 과목별 진행률 (%)
        double koreanProgress,
        double mathProgress,
        double englishProgress
) {

}
