package com.blaybus.blaybusbe.domain.comment.dto.response;

import com.blaybus.blaybusbe.domain.comment.entity.Answer;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String comment;
    private String authorName;
    private Role role;
    private LocalDateTime createdAt;

    public static CommentResponse from(Answer answer) {
        return CommentResponse.builder()
                .id(answer.getId())
                .comment(answer.getComment())
                .authorName(answer.getUser().getName())
                .role(answer.getUser().getRole())
                .createdAt(answer.getCreatedAt())
                .build();
    }
}
