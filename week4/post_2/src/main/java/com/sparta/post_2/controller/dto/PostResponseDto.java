package com.sparta.post_2.controller.dto;

import com.sparta.post_2.model.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String writer;
    private String content;
    private String createdDate;
    private String modifiedDate;
    private List<CommentResponseDto> comments;
    private MemberDto member;

    public PostResponseDto(Post posts){
        this.id = posts.getId();
        this.title = posts.getTittle();
        this.writer = posts.getAuthor();
        this.content = posts.getContent();
        this.createdDate = posts.getCreatedAt().toString();
        this.modifiedDate = posts.getModifiedAt().toString();
        this.comments = posts.getComments().stream().map(CommentResponseDto::new).collect(Collectors.toList());
        this.member = new MemberDto(posts.getMember());
    }
}
