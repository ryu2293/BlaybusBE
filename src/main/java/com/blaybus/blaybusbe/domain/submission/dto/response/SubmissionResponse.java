package com.blaybus.blaybusbe.domain.submission.dto.response;

import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import com.blaybus.blaybusbe.domain.submission.entity.TaskSubmission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SubmissionResponse {

    private Long id;
    private List<String> fileUrls;
    private String menteeComment;
    private LocalDateTime createdAt;
    private String menteeName;

    public static SubmissionResponse from(TaskSubmission submission) {
        List<String> fileUrls = submission.getImages().stream()
                .map(SubmissionImage::getImageUrl)
                .toList();

        return SubmissionResponse.builder()
                .id(submission.getId())
                .fileUrls(fileUrls)
                .menteeComment(submission.getMenteeComment())
                .createdAt(submission.getCreatedAt())
                .menteeName(submission.getTask().getMentee().getName())
                .build();
    }
}
