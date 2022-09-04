package sparta.drawmydaily_backend.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //comment의 고유의 id 값 -> comment_id라 생각

    @Column(nullable = false)
    private String content; // 댓글 내용

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; //댓글 작성 자 (회원가입된 유저)

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post; //작성하는 댓글이 어느 게시글 댓글인지


    public boolean validateUser(User user) {
        return !this.user.equals(user);
    }
}
