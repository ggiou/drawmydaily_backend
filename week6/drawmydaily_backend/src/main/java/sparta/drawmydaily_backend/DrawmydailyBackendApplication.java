package sparta.drawmydaily_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DrawmydailyBackendApplication {
    public static void main(String[] args) {

        SpringApplication.run(DrawmydailyBackendApplication.class, args);
    }

}
