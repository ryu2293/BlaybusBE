package com.blaybus.blaybusbe.domain.submission.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateSubmissionRequest {

    @NotEmpty(message = "이미지 URL 목록은 필수입니다.")
    private List<String> fileUrls;

    private String menteeComment;
}
