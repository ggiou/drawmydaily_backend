package com.example.mypost.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 댓글 출력용 Dto
@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;


}
