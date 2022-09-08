package sparta.drawmydaily_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.drawmydaily_backend.controller.response.MyActivityDto;
import sparta.drawmydaily_backend.controller.response.MyPageReponseDto;
import sparta.drawmydaily_backend.controller.response.PostResponseDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.Post;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.repository.PostRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final TokenProvider tokenProvider;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public ResponseDto<?> getMypage(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        // 리프레시 토큰을 이용해서 유저정보찾기

        Users users = validateUser(request);
        if (null == users){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        //user가 유효한지 확인 = 로그인된 회원 확인, 리프레시 토큰을 이용해 유저정보 찾기

        //자신이 쓴 게시글 기준 찾기
        //1. 자신이 쓴 게시글을 전부 가져와 dto 리스트로 저장
        List<Post> postList = postRepository.findByUsers(users);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList){
            postResponseDtoList.add(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .date(post.getDate())
                            .user_name(post.getUsers().getName())
                            .sayMe(post.getSayMe())
                            .imageURL(post.getImageURL())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

        //2. 작성한 글 responseDto 생성
        MyActivityDto myActivityDto = MyActivityDto.builder()
                .name(users.getName())
                .postResponseDtoList(postResponseDtoList)
                .build();

        //3. MyPageResponseDto에 MyActivityDto 넣고 리턴
        return ResponseDto.success(new MyPageReponseDto((myActivityDto)));
    }

    private Users validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }
    // 토큰 유효 확인 -> 인증된, 로그인된 유저인지 찾기
}
//my 페이지는 home 페이지와 같은 형식으로 단순히 내가 작성한 게시글을
//전체 보여준다. 여기서 각 게시글을 클릭시 그 게시글의 detail 로 이동-> pront 담당