package com.sparta.post_2.controller.response;

import com.sparta.post_2.controller.request.PostRequestDto;
import com.sparta.post_2.controller.request.ResponseDto;
import com.sparta.post_2.model.Post;
import com.sparta.post_2.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/post/create")
    public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto){
        return postService.createPost(requestDto);
    }

    @PutMapping("/post/boidfy/{id}")
    public ResponseDto<?> modifyPost(@RequestBody PostRequestDto requestDto, @PathVariable Long id){
        return postService.modifyPost(requestDto, id);
    }

    @DeleteMapping("/post/delete/{id}")
    public ResponseDto<?> deletePost(@PathVariable Long id){
        System.out.println("난 가버린 메모야.. 죽어써");
        return postService.deletePost(id);
    }
}
