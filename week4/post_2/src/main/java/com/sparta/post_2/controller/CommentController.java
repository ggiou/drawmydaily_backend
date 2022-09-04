package com.sparta.post_2.controller.response;

import com.sparta.post_2.controller.request.CommentDto;
import com.sparta.post_2.controller.request.CommentRequestDto;
import com.sparta.post_2.controller.request.ResponseDto;
import com.sparta.post_2.model.Comment;
import com.sparta.post_2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/comment")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create/{id}")
    public ResponseDto<?> create_comment(@RequestBody CommentRequestDto commentDto, @PathVariable Long id){
        return commentService.createComment(commentDto,id);
    }

    @PutMapping("/modify/{id}")
    public ResponseDto<?> modify_comment(@RequestBody CommentDto commentDto, @PathVariable Long id){
        return commentService.modifyComment(commentDto, id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDto<?> delete_comment(@PathVariable Long id){
        System.out.println("난 죽는다.. 으악");
        return commentService.deleteComment(id);
    }

}
