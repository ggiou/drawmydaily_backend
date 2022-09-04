package com.sparta.post_2.controller.dto;

import com.sparta.post_2.model.Comment;
import com.sparta.post_2.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private Long id;
    private String comment;
    private Post posts;

    private String author;

    public Comment toEntity(){
        Comment comments = Comment.builder()
                .id(id)
                .comment(comment)
                .posts(posts)
                .author(author)
                .build();
        return comments;
    }
}
