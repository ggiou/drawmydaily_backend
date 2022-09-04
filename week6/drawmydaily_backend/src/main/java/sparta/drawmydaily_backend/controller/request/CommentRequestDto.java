package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long parentId;
    //댓글은 작성된 게시글(postId-> 부모테이블)에 달려야하니 어느 게시글에 달 댓글인지 parentId 알아야함
    private String content;
}
