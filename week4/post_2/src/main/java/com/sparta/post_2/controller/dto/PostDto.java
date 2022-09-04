package com.sparta.post_2.controller.request;

import com.sparta.post_2.model.Post;

import java.util.List;

public class PostDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private String createdDate;
    private String modifiedDate;
    private List<CommentResponseDto> comments;

    public PostDto(Post posts){
        this.id = posts.getId();
        this.title = posts.getTittle();
        this.author = posts.getAuthor();
        this.content = posts.getContent();
        this.createdDate = posts.getCreatedAt().toString();
        this.modifiedDate = posts.getModifiedAt().toString();
    }
}
