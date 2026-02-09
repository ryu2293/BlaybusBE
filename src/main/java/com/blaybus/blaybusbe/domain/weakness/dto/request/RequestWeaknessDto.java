package com.blaybus.blaybusbe.domain.weakness.dto.request;

import com.blaybus.blaybusbe.domain.weakness.enums.Subject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestWeaknessDto(
        @Schema(description = "보완점 제목", example = "자료구조 보완")
        @NotBlank
        String title,

        @NotNull(message = "멘티 식별자가 필요합니다.")
        Long menteeId,

        @Schema(description = "과목", example = "KOREAN")
        @NotNull
        Subject subject,

        @NotNull
        Long contentId
) {
}
