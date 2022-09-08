package sparta.drawmydaily_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparta.drawmydaily_backend.controller.request.CommentRequestDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.service.CommentService;

import javax.servlet.http.HttpServletRequest;

@Validated //유효성 검증
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @RequestMapping(value = "/api/auth/comments", method = RequestMethod.POST)
    public ResponseDto<?> createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest request){
        return commentService.createComment(requestDto, request);
    }//로그인 된 유저만 댓글 작성 가능 -> detail 페이지

    @RequestMapping(value = "/api/comments/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getAllComments(@PathVariable Long id){
        return commentService.getAllCommentsByPost(id);
    }//게시글에 작성된 댓글 모두 가져오기 -> detail 페이지(모두 볼 수 있음)

    @RequestMapping(value = "/api/auth/comments/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return commentService.updateComment(id, requestDto, request);
    }

    @RequestMapping(value = "/api/auth/comments/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteComment(@PathVariable Long id, HttpServletRequest request){
        return commentService.deleteComment(id, request);
    }//댓글 작성한 사용자가 삭제 -> detail 페이지
}
