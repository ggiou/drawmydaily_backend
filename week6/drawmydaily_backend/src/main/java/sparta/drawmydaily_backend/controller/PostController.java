package sparta.drawmydaily_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.drawmydaily_backend.controller.request.PostRequestDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.UserDetailsImpl;
import sparta.drawmydaily_backend.service.PostService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @RequestMapping(value = "/api/auth/posts", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        return postService.createPost(postRequestDto, request);
    } //회원가입 된 유저만 게시글 작성 기능 -> add 페이지

//    @RequestMapping(value = "/api/auth/upload", method = RequestMethod.POST, consumes = {"multipart/form-data"})
//    public ResponseDto<?> uploadImg(@RequestPart("file")MultipartFile file, HttpServletRequest request){
//        return postService.uploadImg(file, request);
//    } //이미지 s3서버에 올리기-> add 페이지, 이미지 업로드 된 후 그 url을 받아와 출력


    @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts(){
        return postService.getAllPost();
    } //게시글 전체 조회 -> Home 페이지(권한 없이도 볼 수 있음)

    @RequestMapping(value = "/api/detail/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long id){
        return postService.getPost(id);
    }//상세 게시글 조회 -> Detail 페이지(권한 없이도 볼 수 있음)

    @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updatePost(@PathVariable Long id, @ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        return postService.updatePost(id, postRequestDto,  request);
    }//해당 글을 작성한 유저만 게시글 수정 -> Detail 페이지(작성자만 수정버튼), add 페이지(아마 그대로 사용?)

    @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long id, HttpServletRequest request) throws IOException {
        return postService.deletePost(id, request);
    }//해당 글을 작성한 유저만 게시글 삭제 -> Detail 페이지(작성자만 삭제버튼)
}
