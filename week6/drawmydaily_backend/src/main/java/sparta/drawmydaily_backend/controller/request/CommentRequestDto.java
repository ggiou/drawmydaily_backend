package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long postId;
    //댓글은 작성된 게시글(postId)에 달려야하니 어느 게시글에 달 댓글인지 parentId 알아야함
    @Size(max = 50, message = "댓글의 내용은 50자 이하로 입력할 수 있습니다.")
    private String content;
}
