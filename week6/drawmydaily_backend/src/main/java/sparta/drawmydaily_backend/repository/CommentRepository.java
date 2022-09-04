package sparta.drawmydaily_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.drawmydaily_backend.domain.Comment;
import sparta.drawmydaily_backend.domain.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPost(Post post);
   //특정 게시글의 모든 댓글 찾기
}
