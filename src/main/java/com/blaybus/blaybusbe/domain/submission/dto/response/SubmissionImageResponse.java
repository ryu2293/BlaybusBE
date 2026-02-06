package com.blaybus.blaybusbe.domain.submission.dto.response;

import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionImageResponse {

    private Long id;
    private String imageUrl;

    public static SubmissionImageResponse from(SubmissionImage image) {
        return SubmissionImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
