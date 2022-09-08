package sparta.drawmydaily_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long postId;
    private String user_name;
    private String content;
    private LocalDateTime createdAt; //작성시간
    private LocalDateTime modifiedAt; //수정시간
}
