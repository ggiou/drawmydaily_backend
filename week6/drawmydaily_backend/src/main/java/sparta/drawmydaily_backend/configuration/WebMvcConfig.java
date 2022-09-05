package sparta.drawmydaily_backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") //origin이 localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") //허용하는 메소드
                .allowedHeaders("*") //헤더
                .allowCredentials(true) //인증 정보
                .maxAge(MAX_AGE_SECS);
    }
}
