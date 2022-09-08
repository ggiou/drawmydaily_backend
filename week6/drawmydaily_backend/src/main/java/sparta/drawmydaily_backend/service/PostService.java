package sparta.drawmydaily_backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.drawmydaily_backend.controller.request.PostRequestDto;
import sparta.drawmydaily_backend.controller.response.CommentResponseDto;
import sparta.drawmydaily_backend.controller.response.PostResponseDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.Comment;
import sparta.drawmydaily_backend.domain.Post;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.repository.CommentRepository;
import sparta.drawmydaily_backend.repository.PostRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;
    private final AmazonS3Service amazonS3Service;

    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")  // 내 S3 버켓 이름!!
    private String bucketName;


    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인

        Users users = validateUser(request);
        if (null == users) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        String imageURL;
        MultipartFile multipartFile = postRequestDto.getImage();

        if (!multipartFile.isEmpty()) { // 삽입 할 이미지가 있다면
            ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile);
            if (!responseDto.isSuccess())
                return responseDto;
            imageURL = (String) responseDto.getData();

        } else { //삽입 할 이미지가 없다면
            String fileName = "noImage.png"; //기본 이미지 넣기
            imageURL = amazonS3Client.getUrl(bucketName, fileName).toString(); // s3에 미리 저장해논 기본 이미지 URL 대입
        }

        int strlen = postRequestDto.getContent().length();
        if (strlen>300){
            return ResponseDto.fail("CONTETN_LENGTH", "내용은 300자 이상 입력하실수 없습니다.");
        }
        //content 글자 제한

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .date(postRequestDto.getDate())
                .content(postRequestDto.getContent())
                .sayMe(postRequestDto.getSayMe())
                .imageURL(imageURL)
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
                        .imageURL(post.getImageURL())    //이미지는 url로 전달
                        .createdAt(post.getCreatedAt())     //작성 시간
                        .modifiedAt(post.getModifiedAt())   //수정 시간
                        .build()
        );


    }
    //로그인한 회원의 게시글 작성


    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> postsList = postRepository.findAllByOrderByModifiedAtDesc();

        List<PostResponseDto> list = new ArrayList<>();

        for (Post post : postsList) {
            list.add(PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .date(post.getDate())
                    .user_name(post.getUsers().getName())
                    .content(post.getContent())
                    .sayMe(post.getSayMe())
                    .imageURL(post.getImageURL())
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
        if (null == post) {
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
                        .imageURL(post.getImageURL())
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
    public ResponseDto<?> updatePost(Long id, PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인, 오류 처리

        Users users = validateUser(request);
        if (null == users) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        if (post.validateUser(users)) {
            return ResponseDto.fail("BAD_REQUEST", "해당 글의 작성자만 수정할 수 있습니다.");
        } //해당 id 게시글 작성자 확인

        int strlen = postRequestDto.getContent().length();
        if (strlen > 300) {
            return ResponseDto.fail("POST_CONTETN_LENGTH", "게시글 내용은 300자 이상 입력하실수 없습니다.");
        }
        //글자 수 제한

        MultipartFile multipartFile = postRequestDto.getImage();
        String imageURL = null;

        if (post.getImageURL().equals("https://ggiou.s3.ap-northeast-2.amazonaws.com/noImage.png")) { //이전 게시글에 이미지가 없는 경우
            if (!multipartFile.isEmpty()) {//수정 게시글에 이미지를 넣는 경우
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile);
                if (!responseDto.isSuccess())
                    return responseDto;
                imageURL = (String) responseDto.getData();
            } else { //수정 게시글에 이미지를 안 넣는 경우
                imageURL = postRepository.findById(id).get().getImageURL();
            }
        } else { //이전 게시글에 이미지가 있는 경우
            amazonS3Service.removeFile(postRepository.findById(id).get().getImageURL()); //기존 이미지 삭제
            if (!multipartFile.isEmpty()) {//수정 게시글에 이미지를 넣는 경우
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile);
                if (!responseDto.isSuccess())
                    return responseDto;
                imageURL = (String) responseDto.getData();
            } else { //수정 게시글에 이미지를 안 넣는 경우
                imageURL = postRepository.findById(id).get().getImageURL();
            }
        }

        post.update(postRequestDto, imageURL, users);

        return ResponseDto.success(post);
    }
    //id의 게시글 수정

    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) throws IOException {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인, 오류 처리

        Users users = validateUser(request);
        if (null == users) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        } //user가 유효한지 확인 = 로그인된 회원 확인

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        } //해당 id의 게시글 없으면 오류 발생

        if (post.validateUser(users)) {
            return ResponseDto.fail("BAD_REQUEST", "해당 글의 작성자만 수정할 수 있습니다.");
        } //해당 id 게시글 작성자 확인

        // 만약 매핑테이블을 사용하지 않는다면 바로 url에서 파일이름을 빼오면 된다.

//        String imageName = post.getImage().getUrl().substring(post.getImage().getUrl().lastIndexOf("com/")+4);
//        System.out.println(imageName);
//        System.out.println(post.getImage().getName());

        if (!post.getImageURL().equals("https://ggiou.s3.ap-northeast-2.amazonaws.com/noImage.png")) {

                amazonS3Service.removeFile(postRepository.findById(id).get().getImageURL());

                postRepository.delete(post);
            } // 기본 이미지가 아니면 삭제

        return ResponseDto.success(id);
    }

        //id의 게시글 삭제

        @Transactional
        public Users validateUser (HttpServletRequest request){
            if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
                return null;
            }
            return tokenProvider.getUserFromAuthentication();
        }
        //token 확인을 통해 로그인 여부 확인

        @Transactional(readOnly = true)
        public Post isPresentPost (Long id){
            Optional<Post> optionalPost = postRepository.findById(id);
            return optionalPost.orElse(null);
        }
        //만약 게시글 repository에 해당 id의 게시글이 있으면 그대로, 없으면 null 전송 = 존재 여부 확인
    }
