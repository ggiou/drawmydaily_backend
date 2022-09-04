package sparta.drawmydaily_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.drawmydaily_backend.controller.request.PostRequestDto;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //post id

    @Column(nullable = false)
    private String title; //제목

    @Column(nullable = false)
    private String content; //작성 글 내용

    @Column(nullable = false)
    private String date; //작성 시간: string으로 받아와 출력만 할 거

    @Column(nullable = false)
    private String sayMe; //나에게 할 말

    @OneToOne
    private ImageMapper image; // 이미지

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments; //댓글리스트

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; //회원가입 된 유저

    public boolean validateUser(User user) {
        return !this.user.equals(user);
    }
    ////작성자 확인

    public void update(PostRequestDto postRequestDto, ImageMapper imageMapper) {
        this.title = postRequestDto.getTitle();
        this.date = postRequestDto.getDate();
        this.image = image;
        this.content = postRequestDto.getContent();
        this.sayMe = postRequestDto.getSayMe();
    }
    //게시물 수정

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    //게시글에 댓글 생성시 댓글리스트에 추가
    public void removeCommentList(Comment comment) {
        comments.remove(comment);
        comment.setPost(this);
    }
    //게시글에 댓글 삭제시 댓글리스트에서 제거



    //name,


}
