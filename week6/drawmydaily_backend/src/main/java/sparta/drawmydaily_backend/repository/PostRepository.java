package sparta.drawmydaily_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.drawmydaily_backend.domain.Post;
import sparta.drawmydaily_backend.domain.Users;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();
    //수정 날짜로 찾기
    List<Post> findByUsers(Users users);

    Optional<Post> findByImageURL(String imageURL);


    //작성 회원으로 찾기
}
