package com.blaybus.blaybusbe.domain.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CreateCommentRequest {

    @Schema(description = "댓글 내용", example = "이 부분 다시 풀어봤는데 맞나요?")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String comment;
}
