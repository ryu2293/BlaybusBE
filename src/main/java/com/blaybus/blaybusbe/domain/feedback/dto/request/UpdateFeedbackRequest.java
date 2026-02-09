package com.blaybus.blaybusbe.domain.feedback.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class UpdateFeedbackRequest {

    @Schema(description = "피드백 내용 (<강조>...</강조> 태그로 강조 가능)", example = "수정된 피드백 내용입니다.")
    @NotBlank(message = "피드백 내용은 필수입니다.")
    private String content;

    @Schema(description = "첨부 이미지 URL (선택)", example = "https://s3-bucket.../feedback/uuid.jpg")
    private String imageUrl;

    @Schema(description = "X 좌표 (0.0~1.0 비율)", example = "0.35")
    @NotNull(message = "X 좌표는 필수입니다.")
    private Float xPos;

    @Schema(description = "Y 좌표 (0.0~1.0 비율)", example = "0.72")
    @NotNull(message = "Y 좌표는 필수입니다.")
    private Float yPos;
}
