package sparta.drawmydaily_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.drawmydaily_backend.domain.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
    //회원의 고유 id로 찾기
    Optional<Users> findByName(String name);
    //회원의 가입시 id로 찾기
}
