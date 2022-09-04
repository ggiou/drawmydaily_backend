package sparta.drawmydaily_backend.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.drawmydaily_backend.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    //회원의 고유 id로 찾기
    Optional<User> findByName(String name);
    //회원의 가입시 id로 찾기
}
