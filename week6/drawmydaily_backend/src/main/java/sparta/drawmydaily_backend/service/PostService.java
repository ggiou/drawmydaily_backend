package sparta.drawmydaily_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.drawmydaily_backend.controller.request.PostRequestDto;
import sparta.drawmydaily_backend.controller.response.CommentResponseDto;
import sparta.drawmydaily_backend.controller.response.PostListResponseDto;
import sparta.drawmydaily_backend.controller.response.PostResponseDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.Comment;
import sparta.drawmydaily_backend.domain.ImageMapper;
import sparta.drawmydaily_backend.domain.Post;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.repository.CommentRepository;
import sparta.drawmydaily_backend.repository.ImageMapperRepository;
import sparta.drawmydaily_backend.repository.PostRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AmazonS3Service amazonS3Service;
    private final ImageMapperRepository imageMapperRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인

        Users users = validateUser(request);
        if (null == users){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Optional<ImageMapper> findImage = imageMapperRepository.findByUrl(requestDto.getImgURL());
        if(findImage.isEmpty())
            return ResponseDto.fail("URL_ERROR", "이미지 URL이 올바르지 않습니다.");
        //업로드한 이미지 URL을 받아와 유효한지 확인

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .date(requestDto.getDate())
                .content(requestDto.getContent())
                .sayMe(requestDto.getSayMe())
                .image(findImage.get())
                .users(users)   //-> 로그인한 회원 정보 유효한지 확인 후 작성자
                .build();
        postRepository.save(post); //게시글 작성후 repostory 저장
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .date(post.getDate())
                        .user_name(post.getUsers().getName())
                        .content(post.getContent())
                        .sayMe(post.getSayMe())
                        .image(post.getImage().getUrl())    //이미지는 url로 전달
                        .createdAt(post.getCreatedAt())     //작성 시간
                        .modifiedAt(post.getModifiedAt())   //수정 시간
                        .build()
        );
    }
    //로그인한 회원의 게시글 작성

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

        List<PostListResponseDto> list = new ArrayList<>();
        for (Post post : posts){
            list.add(PostListResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .date(post.getDate())
                    .user_name(post.getUsers().getName())
                    .sayme(post.getSayMe())
                    .image(post.getImage().getUrl())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
            );
        }
        return ResponseDto.success(list);
    }
    //Home 페이지의 게시글 리스트 불러오기

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Post post = isPresentPost(id);
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>(); //comment list 담을 리스트 생성

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .user_name(comment.getUsers().getName())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        } //해당 게시글의 commentlist 생성

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .date(post.getDate())
                        .user_name(post.getUsers().getName())
                        .image(post.getImage().getUrl())
                        .content(post.getContent())
                        .sayMe(post.getSayMe())
                        .commentResponseDtoList(commentResponseDtoList)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );

    }
    //detail 페이지의 id의 게시글, 댓글 조회

    @Transactional
    public ResponseDto<?> updatePost(Long id, PostRequestDto postRequestDto, MultipartFile file, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인, 오류 처리

        Users users = validateUser(request);
        if (null == users){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Post post = isPresentPost(id);
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        if (post.validateUser(users)){
            return ResponseDto.fail("BAD_REQUEST", "해당 글의 작성자만 수정할 수 있습니다.");
        } //해당 id 게시글 작성자 확인

        if (amazonS3Service.removeFile(post.getImage().getImgName()))
            return ResponseDto.fail("BAD_REQUEST", "삭제 오류 발생");
        ResponseDto<?>responseDto = amazonS3Service.uploadFile(file); // s3 파일 업로드
        if (!responseDto.isSuccess())
            return responseDto;
        ImageMapper imageMapper = (ImageMapper)responseDto.getData(); //이미지 변경, 및 추가 과정

        post.update(postRequestDto, imageMapper);
        return ResponseDto.success(post);
    }
    //id의 게시글 수정

    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인, 오류 처리

        Users users = validateUser(request);
        if (null == users){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Post post = isPresentPost(id);
        if (null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        if (post.validateUser(users)){
            return ResponseDto.fail("BAD_REQUEST", "해당 글의 작성자만 수정할 수 있습니다.");
        } //해당 id 게시글 작성자 확인

        // 만약 매핑테이블을 사용하지 않는다면 바로 url에서 파일이름을 빼오면 된다.

//        String imageName = post.getImage().getUrl().substring(post.getImage().getUrl().lastIndexOf("com/")+4);
//        System.out.println(imageName);
//        System.out.println(post.getImage().getName());

        if (amazonS3Service.removeFile(post.getImage().getImgName()))
            return ResponseDto.fail("BAD_REQUEST", "삭제 오류 발생");
        postRepository.delete(post);
        return ResponseDto.success("성공적으로 삭제되었습니다.");
    }
    //id의 게시글 삭제

    public ResponseDto<?> uploadImg(MultipartFile file, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인, 오류 처리

        Users users = validateUser(request);
        if(null == users){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        // 만약 파일매핑 테이블을 사용하지 않는다면 url로 반환값을 받고 Post에 url를 저장하며 추후 파일 이름이 필요한 부분에서 substring을 활용해 뒷부분의 파일이름만 잘라내면된다.
        // 위의 방식은 데이터베이스공간낭비가 적어지며 코드가 단순해진다. 매번 데이터베이스에서 정보를 불러올 일도 사라짐.
        // 테이블생성 방식을 사용한 이유는 다음과 같다.
        // 1. 업로드된 파일의 관리를 쉽게하기 위하여.
        // 2. 다른 버킷을 이용할때 유연하게 대처하기 위하여
        // 3. 누군가가 내부 코드를 알고있다면 조작된 파일이름을 사용하여 자신이 소유하지 않은 파일을 삭제하려 시도할 수 있음.
        // URL에서 파일이름을 추출하는 코드는 삭제메소드에 구현되어 주석처리 되어있음.
        ResponseDto<?> responseDto = amazonS3Service.uploadFile(file); //s3 파일 업로드
        if (!responseDto.isSuccess())
            return responseDto;
        ImageMapper imageMapper = (ImageMapper)responseDto.getData();
        return ResponseDto.success(imageMapper.getUrl());
        //s3에 이미지 업로드 성공시 url 돌려줘 성공 확인
    }
    //이미지 업로드

    @Transactional
    public Users validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }
    //token 확인을 통해 로그인 여부 확인

    @Transactional(readOnly = true)
   public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    //만약 게시글 repository에 해당 id의 게시글이 있으면 그대로, 없으면 null 전송 = 존재 여부 확인
}
