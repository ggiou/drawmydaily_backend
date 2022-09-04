package sparta.drawmydaily_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String user_name;
    private String date;
    private String content;
    private String sayMe;
    private String image;
    private List<CommentResponseDto> commentResponseDtoList;
    //게시글 detail 페이지에 댓글 리스트도 출력
    private LocalDateTime createdAt; //작성시간
    private LocalDateTime modifiedAt; //수정시간
}
