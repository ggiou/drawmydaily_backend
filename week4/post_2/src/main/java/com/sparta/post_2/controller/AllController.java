package com.sparta.post_2.controller.response;

import com.sparta.post_2.controller.request.ResponseDto;
import com.sparta.post_2.service.AllService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AllController {
    private final AllService allService;
    @GetMapping("/all/post")
    public ResponseDto<?> getAllPosts(){
        return allService.getAllPost();
    }
    @GetMapping("/all/post/{id}")
    public ResponseDto<?> getPost(@PathVariable Long id){
        return allService.getPost(id);
    }

    @GetMapping("/all/comment")
    public ResponseDto<?> getComment(){
        return allService.getAllComment();

    }
}
