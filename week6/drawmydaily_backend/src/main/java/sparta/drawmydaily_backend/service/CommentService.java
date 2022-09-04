package sparta.drawmydaily_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;
import sparta.drawmydaily_backend.controller.request.CommentRequestDto;
import sparta.drawmydaily_backend.controller.response.CommentResponseDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.Comment;
import sparta.drawmydaily_backend.domain.Post;
import sparta.drawmydaily_backend.domain.User;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.repository.CommentRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;
    private final PostService postService;

    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {

        //로그인된 회원인지 확인, 오류 처리
        User user = validateUser(request);
        if (null == user){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Post post = postService.isPresentPost(requestDto.getParentId());
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(requestDto.getContent())
                .build();
        commentRepository.save(comment); //작성한 댓글 내용 repository 저장
        post.addComment(comment); //작성한 댓글, 해당 게시글에도 추가
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .user_name(comment.getUser().getName())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }
    //댓글 작성

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCommentsByPost(Long postId) {
        Post post = postService.isPresentPost(postId);
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for(Comment comment : commentList){
           commentResponseDtoList.add(
                   CommentResponseDto.builder()
                           .id(comment.getId())
                           .user_name(comment.getUser().getName())
                           .content(comment.getContent())
                           .createdAt(comment.getCreatedAt())
                           .modifiedAt(comment.getModifiedAt())
                           .build()
           );
        } //해당 게시글의 댓글 리스트 생성
        return ResponseDto.success(commentResponseDtoList);
    }
    //id의 게시글의 댓글 전체 가져오기

    @Transactional
    public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {


        //로그인된 회원인지 확인, 오류 처리
        User user = validateUser(request);
        if (null == user){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인, 리프레시 토큰을 이용해 유저정보 찾기

        Comment comment = isPresentComment(id);
        if (null == comment){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }
        Post post = postService.isPresentPost(comment.getPost().getId());
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        if(comment.validateUser(user)){
            return ResponseDto.fail("BAD_REQUEST", "해당 댓글의 작성자만 삭제할 수 있습니다.");
        }

        post.removeCommentList(comment);
        commentRepository.delete(comment);
        return ResponseDto.success("성공적으로 삭제되었습니다.");
    }
    //id의 댓글 삭제

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
    //존재하는 게시글인지 확인

    @Transactional
    public User validateUser(HttpServletRequest request) {
        //유효한 토큰을 준 유저만이 로그인 성공, 리프레시, 권한 토큰 받아와야함
        //tokenprovider와 jwt 인증 및 인가를 통해 ...
    }
}
