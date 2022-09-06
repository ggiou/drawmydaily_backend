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
                .maxAge(MAX_AGE_SECS)
                .exposedHeaders("*"); //cors환경에서 헤더 값 제대로 반환을 위해 헤더 정보 출력
                //서버가 요청에 대한 응답으로 브라우저에서 실행되는 스크립트에 제공되어야하는 표시
    }
}
//백엔드 서버를 따로 두고 클라이언트에서 백엔드 서버로 요청하는
//시스템이라 도메인이 달라 발생하는 Cross-Origin Resource Sharing (CORS) 부분을 처리
//REST API 를 설계하는 경우에 특히 필수 처리