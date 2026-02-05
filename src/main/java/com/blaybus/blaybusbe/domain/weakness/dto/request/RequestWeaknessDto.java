package com.blaybus.blaybusbe.domain.weakness.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestWeaknessDto(
        @NotBlank
        String title,

        @NotNull(message = "멘티 식별자가 필요합니다.")
        Long menteeId,

        @NotNull
        Long contentId
) {
}
