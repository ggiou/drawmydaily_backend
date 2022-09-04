package sparta.drawmydaily_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.drawmydaily_backend.domain.ImageMapper;

import java.util.Optional;

public interface ImageMapperRepository extends JpaRepository<ImageMapper, Long> {
    Optional<ImageMapper> findByName(String name);

    Optional<ImageMapper> findByUrl(String url);
}
